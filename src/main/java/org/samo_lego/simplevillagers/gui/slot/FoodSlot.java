package org.samo_lego.simplevillagers.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.world.entity.npc.Villager.FOOD_POINTS;

public class FoodSlot extends Slot {
    public FoodSlot(Container container, int i) {
        super(container, i, 0, 0);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return FOOD_POINTS.containsKey(stack.getItem());
    }
}
