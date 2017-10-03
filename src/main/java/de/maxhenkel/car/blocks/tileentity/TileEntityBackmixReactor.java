package de.maxhenkel.car.blocks.tileentity;

import cofh.api.energy.IEnergyReceiver;
import de.maxhenkel.car.Config;
import de.maxhenkel.car.blocks.ModBlocks;
import de.maxhenkel.car.fluids.ModFluids;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class TileEntityBackmixReactor extends TileEntityBase
		implements ITickable, IFluidHandler, IEnergyReceiver, IInventory {

	public final int maxStorage;
	protected int storedEnergy;
	public final int energyUsage;

	public final int methanolUsage;
	public final int maxMethanol;
	protected int currentMethanol;

	public final int canolaUsage;
	public final int maxCanola;
	protected int currentCanola;

	public final int maxMix;
	protected int currentMix;

	public final int mixGeneration;

	public final int generatingTime;
	protected int timeToGenerate;

	public TileEntityBackmixReactor() {
		this.maxStorage = Config.backmixReactorEnergyStorage;
		this.storedEnergy = 0;
		this.energyUsage = Config.backmixReactorEnergyUsage;

		this.maxMethanol = Config.backmixReactorFluidStorage;
		this.maxCanola = Config.backmixReactorFluidStorage;
		this.maxMix = Config.backmixReactorFluidStorage;

		this.currentCanola = 0;
		this.currentMethanol = 0;
		this.currentMix = 0;

		this.generatingTime = Config.backmixReactorGeneratingTime;
		this.timeToGenerate = 0;

		this.mixGeneration = Config.backmixReactorMixGeneration;
		this.methanolUsage = Config.backmixReactorMethanolUsage;
		this.canolaUsage = Config.backmixReactorCanolaUsage;
	}

	@Override
	public void update() {
		if (world.isRemote) {
			return;
		}

		if (isEnabled()) {
			setBlockEnabled(true);
		} else {
			setBlockEnabled(false);
		}

		if (timeToGenerate > 0 && storedEnergy >= energyUsage) {
			storedEnergy -= energyUsage;
			timeToGenerate--;
			

			if (timeToGenerate == 0) {

				if (currentMix + mixGeneration <= maxMix) {
					currentMix += mixGeneration;
					currentCanola-=canolaUsage;
					currentMethanol-=methanolUsage;
				}
			}
		} else if (storedEnergy >= energyUsage) {
			if (currentCanola >= canolaUsage) {
				if (currentMethanol >= methanolUsage) {
					if (currentMix + mixGeneration <= maxMix) {
						timeToGenerate = generatingTime;
					}
				}
			}
		}
		markDirty();
	}

	public boolean isEnabled() {
		if (storedEnergy > 0 && currentMix < maxMix) {
			if(currentMethanol >= methanolUsage){
				if(currentCanola >= canolaUsage){
					return true;
				}
			}
		}
		return false;
	}

	public void setBlockEnabled(boolean enabled) {
		IBlockState state = world.getBlockState(getPos());
		if (state.getBlock().equals(ModBlocks.BACKMIX_REACTOR)) {
			ModBlocks.BACKMIX_REACTOR.setPowered(world, pos, state, enabled);
		}
	}

	@Override
	public int getField(int id) {
		switch (id) {
		case 0:
			return storedEnergy;
		case 1:
			return currentCanola;
		case 2:
			return currentMethanol;
		case 3:
			return currentMix;
		case 4:
			return timeToGenerate;
		}

		return 0;
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case 0:
			storedEnergy = value;
			break;
		case 1:
			currentCanola = value;
			break;
		case 2:
			currentMethanol = value;
			break;
		case 3:
			currentMix = value;
			break;
		case 4:
			timeToGenerate = value;
			break;
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("stored_endergy", storedEnergy);
		compound.setInteger("canola", currentCanola);
		compound.setInteger("methanol", currentMethanol);
		compound.setInteger("mix", currentMix);
		compound.setInteger("time", timeToGenerate);
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		storedEnergy=compound.getInteger("stored_endergy");
		currentCanola=compound.getInteger("canola");
		currentMethanol=compound.getInteger("methanol");
		currentMix=compound.getInteger("mix");
		timeToGenerate=compound.getInteger("time");
		super.readFromNBT(compound);
	}

	@Override
	public int getFieldCount() {
		return 5;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int energyNeeded = maxStorage - storedEnergy;

		if (!simulate) {
			storedEnergy += Math.min(energyNeeded, maxReceive);
			markDirty();
		}

		return Math.min(energyNeeded, maxReceive);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return storedEnergy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return maxStorage;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[] { new IFluidTankProperties() {

			@Override
			public FluidStack getContents() {
				return new FluidStack(ModFluids.CANOLA_OIL, currentCanola);
			}

			@Override
			public int getCapacity() {
				return maxCanola;
			}

			@Override
			public boolean canFillFluidType(FluidStack fluidStack) {
				return fluidStack.getFluid().equals(ModFluids.CANOLA_OIL);
			}

			@Override
			public boolean canFill() {
				return true;
			}

			@Override
			public boolean canDrainFluidType(FluidStack fluidStack) {
				return false;
			}

			@Override
			public boolean canDrain() {
				return false;
			}
		}, new IFluidTankProperties() {

			@Override
			public FluidStack getContents() {
				return new FluidStack(ModFluids.METHANOL, currentMethanol);
			}

			@Override
			public int getCapacity() {
				return maxMethanol;
			}

			@Override
			public boolean canFillFluidType(FluidStack fluidStack) {
				return fluidStack.getFluid().equals(ModFluids.METHANOL);
			}

			@Override
			public boolean canFill() {
				return true;
			}

			@Override
			public boolean canDrainFluidType(FluidStack fluidStack) {
				return false;
			}

			@Override
			public boolean canDrain() {
				return false;
			}
		}, new IFluidTankProperties() {

			@Override
			public FluidStack getContents() {
				return new FluidStack(ModFluids.CANOLA_METHANOL_MIX, currentMix);
			}

			@Override
			public int getCapacity() {
				return maxMix;
			}

			@Override
			public boolean canFillFluidType(FluidStack fluidStack) {
				return false;
			}

			@Override
			public boolean canFill() {
				return false;
			}

			@Override
			public boolean canDrainFluidType(FluidStack fluidStack) {
				return fluidStack.getFluid().equals(ModFluids.CANOLA_METHANOL_MIX);
			}

			@Override
			public boolean canDrain() {
				return true;
			}
		} };
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (resource.getFluid().equals(ModFluids.METHANOL)) {
			int amount = Math.min(maxMethanol - currentMethanol, resource.amount);
			if (doFill) {
				currentMethanol += amount;
				markDirty();
			}
			return amount;
		} else if (resource.getFluid().equals(ModFluids.CANOLA_OIL)) {
			int amount = Math.min(maxCanola - currentCanola, resource.amount);
			if (doFill) {
				currentCanola += amount;
				markDirty();
			}
			return amount;
		}

		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		int amount = Math.min(resource.amount, currentMix);

		if (doDrain) {
			currentMix -= amount;
			markDirty();
		}

		return new FluidStack(ModFluids.CANOLA_METHANOL_MIX, amount);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		int amount = Math.min(maxDrain, currentMix);

		if (doDrain) {
			currentMix -= amount;
			markDirty();
		}

		return new FluidStack(ModFluids.CANOLA_METHANOL_MIX, amount);
	}

	@Override
	public String getName() {
		return new TextComponentTranslation("tile.backmix_reactor.name").getFormattedText();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {

	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return false;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean isEmpty() {
		return true;
	}

}
