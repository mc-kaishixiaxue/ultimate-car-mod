package de.maxhenkel.car.blocks.tileentity;

import de.maxhenkel.car.Main;
import de.maxhenkel.car.fluids.ModFluids;
import de.maxhenkel.corelib.blockentity.ITickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class TileEntitySplitTank extends TileEntityBase implements ITickableBlockEntity, IFluidHandler, Container {

    private int currentMix;
    public int maxMix;
    public int mixUsage;

    private int currentBioDiesel;
    public int maxBioDiesel;
    public int bioDieselGeneration;

    private int currentGlycerin;
    public int maxGlycerin;
    public int glycerinGeneration;

    public int generatingTime;
    private int timeToGenerate;

    protected SimpleContainer inventory;

    public TileEntitySplitTank(BlockPos pos, BlockState state) {
        super(Main.SPLIT_TANK_TILE_ENTITY_TYPE, pos, state);
        this.inventory = new SimpleContainer(0);
        this.currentMix = 0;
        this.maxMix = Main.SERVER_CONFIG.splitTankFluidStorage.get();

        this.currentBioDiesel = 0;
        this.maxBioDiesel = Main.SERVER_CONFIG.splitTankFluidStorage.get();

        this.currentGlycerin = 0;
        this.maxGlycerin = Main.SERVER_CONFIG.splitTankFluidStorage.get();

        this.generatingTime = Main.SERVER_CONFIG.splitTankGeneratingTime.get();
        this.timeToGenerate = 0;

        this.mixUsage = Main.SERVER_CONFIG.splitTankMixUsage.get();
        this.glycerinGeneration = Main.SERVER_CONFIG.splitTankGlycerinGeneration.get();
        this.bioDieselGeneration = Main.SERVER_CONFIG.splitTankBioDieselGeneration.get();
    }

    public final ContainerData FIELDS = new ContainerData() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return currentMix;
                case 1:
                    return currentBioDiesel;
                case 2:
                    return currentGlycerin;
                case 3:
                    return timeToGenerate;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    currentMix = value;
                    break;
                case 1:
                    currentBioDiesel = value;
                    break;
                case 2:
                    currentGlycerin = value;
                    break;
                case 3:
                    timeToGenerate = value;
                    break;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    @Override
    public void tick() {
        if (level.isClientSide) {
            return;
        }

        if (timeToGenerate > 0) {
            timeToGenerate--;

            if (timeToGenerate == 0) {

                if (currentMix - mixUsage >= 0) {
                    currentMix -= mixUsage;
                    if (currentBioDiesel + bioDieselGeneration <= maxBioDiesel) {
                        currentBioDiesel += bioDieselGeneration;
                    }
                    if (currentGlycerin + glycerinGeneration <= maxGlycerin) {
                        currentGlycerin += glycerinGeneration;
                    }
                }
                // synchronize();
            }
        } else {
            if (currentMix >= mixUsage) {
                if (currentBioDiesel + bioDieselGeneration <= maxBioDiesel) {
                    if (currentGlycerin + glycerinGeneration <= maxGlycerin) {
                        timeToGenerate = generatingTime;
                    }

                }

            }
        }

        if (level.getGameTime() % 200 == 0) {
            synchronize();
        }

        setChanged();
    }

    public float getBioDieselPerc() {
        return ((float) currentBioDiesel) / ((float) maxBioDiesel);
    }

    public float getGlycerinPerc() {
        return ((float) currentGlycerin) / ((float) maxGlycerin);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        compound.putInt("mix", currentMix);
        compound.putInt("bio_diesel", currentBioDiesel);
        compound.putInt("glycerin", currentGlycerin);
        compound.putInt("time", timeToGenerate);
    }

    @Override
    public void load(CompoundTag compound) {
        currentMix = compound.getInt("mix");
        currentBioDiesel = compound.getInt("bio_diesel");
        currentGlycerin = compound.getInt("glycerin");
        timeToGenerate = compound.getInt("timeToGenerate");
        super.load(compound);
    }

    @Override
    public int getContainerSize() {
        return inventory.getContainerSize();
    }

    @Override
    public ItemStack getItem(int index) {
        return inventory.getItem(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return inventory.removeItem(index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return inventory.removeItemNoUpdate(index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        inventory.setItem(index, stack);
    }

    @Override
    public int getMaxStackSize() {
        return inventory.getMaxStackSize();
    }

    @Override
    public boolean stillValid(Player player) {
        return inventory.stillValid(player);
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public void startOpen(Player player) {
        inventory.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        inventory.stopOpen(player);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return inventory.canPlaceItem(index, stack);
    }

    @Override
    public void clearContent() {
        inventory.clearContent();
    }

    public int getCurrentMix() {
        return currentMix;
    }

    public int getCurrentBioDiesel() {
        return currentBioDiesel;
    }

    public int getCurrentGlycerin() {
        return currentGlycerin;
    }

    public int getTimeToGenerate() {
        return timeToGenerate;
    }

    @Override
    public Component getTranslatedName() {
        return new TranslatableComponent("block.car.split_tank");
    }

    @Override
    public ContainerData getFields() {
        return FIELDS;
    }

    @Override
    public int getTanks() {
        return 3;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank == 0) {
            return new FluidStack(ModFluids.CANOLA_METHANOL_MIX, currentMix);
        } else if (tank == 1) {
            return new FluidStack(ModFluids.BIO_DIESEL, currentBioDiesel);
        } else {
            return new FluidStack(ModFluids.GLYCERIN, currentGlycerin);
        }
    }

    @Override
    public int getTankCapacity(int tank) {
        if (tank == 0) {
            return maxMix;
        } else if (tank == 1) {
            return maxBioDiesel;
        } else {
            return maxGlycerin;
        }
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        if (tank == 0) {
            return stack.getFluid().equals(ModFluids.CANOLA_METHANOL_MIX);
        } else if (tank == 1) {
            return stack.getFluid().equals(ModFluids.BIO_DIESEL);
        } else {
            return stack.getFluid().equals(ModFluids.GLYCERIN);
        }
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.getFluid().equals(ModFluids.CANOLA_METHANOL_MIX)) {
            int amount = Math.min(maxMix - currentMix, resource.getAmount());
            if (action.execute()) {
                currentMix += amount;
                setChanged();
            }
            return amount;
        }

        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.getFluid().equals(ModFluids.GLYCERIN)) {
            int amount = Math.min(resource.getAmount(), currentGlycerin);

            if (action.execute()) {
                currentGlycerin -= amount;
                setChanged();
            }

            return new FluidStack(ModFluids.GLYCERIN, amount);
        } else if (resource.getFluid().equals(ModFluids.BIO_DIESEL)) {
            int amount = Math.min(resource.getAmount(), currentBioDiesel);

            if (action.execute()) {
                currentBioDiesel -= amount;
                setChanged();
            }
            return new FluidStack(ModFluids.BIO_DIESEL, amount);
        }

        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (currentGlycerin > currentBioDiesel) {
            int amount = Math.min(maxDrain, currentGlycerin);

            if (action.execute()) {
                currentGlycerin -= amount;
                setChanged();
            }

            return new FluidStack(ModFluids.GLYCERIN, amount);
        } else {
            int amount = Math.min(maxDrain, currentBioDiesel);

            if (action.execute()) {
                currentBioDiesel -= amount;
                setChanged();
            }
            return new FluidStack(ModFluids.BIO_DIESEL, amount);
        }
    }
}
