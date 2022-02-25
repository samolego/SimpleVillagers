package org.samo_lego.simplevillagers.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import static org.samo_lego.simplevillagers.SimpleVillagers.VILLAGER_ITEM;

public class VillagerSlot extends Slot {
    public VillagerSlot(Container container, int i) {
        super(container, i, 0, 0);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(VILLAGER_ITEM);
    }
}
