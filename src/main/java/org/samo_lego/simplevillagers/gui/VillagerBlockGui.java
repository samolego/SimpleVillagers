package org.samo_lego.simplevillagers.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VillagerBlockGui extends SimpleGui {

    public VillagerBlockGui(ServerPlayer player, Container itemContainer, int dividerSlots) {
        super(MenuType.GENERIC_9x1, player, false);

        final ItemStack divider = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        for (int i = 0; i < itemContainer.getContainerSize(); i++) {
            if (i == (itemContainer.getContainerSize() / 2)) {
                for (int j = 0; j < dividerSlots; j++) {
                    this.setSlot(i + j, divider);
                }
            }

            boolean halfWayThere = i >= (itemContainer.getContainerSize() / 2);
            this.setSlotRedirect(halfWayThere ? i + dividerSlots : i, new Slot(itemContainer, i, 0, 0));
        }
    }
}
