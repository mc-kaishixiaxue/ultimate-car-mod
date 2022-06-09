package de.maxhenkel.car.items;

import de.maxhenkel.car.entity.car.parts.Part;
import net.minecraft.world.item.ItemStack;

public class ItemCarPart extends AbstractItemCarPart {

    private final Part part;

    public ItemCarPart(Part part) {
        this.part = part;
    }

    @Override
    public Part getPart(ItemStack stack) {
        return part;
    }
}
