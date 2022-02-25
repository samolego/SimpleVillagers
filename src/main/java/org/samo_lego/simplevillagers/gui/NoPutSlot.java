package org.samo_lego.simplevillagers.gui;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NoPutSlot extends Slot {
    public NoPutSlot(Container container, int index) {
        super(container, index, 0, 0);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }
}
