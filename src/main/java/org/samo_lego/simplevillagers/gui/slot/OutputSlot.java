package org.samo_lego.simplevillagers.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OutputSlot extends Slot {
    public OutputSlot(Container container, int index) {
        super(container, index, 0, 0);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }
}
