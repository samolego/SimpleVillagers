package org.samo_lego.simplevillagers.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Function;

public class VillagerBlockGui extends SimpleGui {

    public VillagerBlockGui(ServerPlayer player, Container itemContainer, Component title, List<ItemStack> dividerSlots, Function<Integer, Slot> inventorySlots) {
        super(MenuType.GENERIC_9x1, player, false);

        for (int i = 0; i < itemContainer.getContainerSize(); i++) {
            if (i == (itemContainer.getContainerSize() / 2)) {
                for (int j = 0; j < dividerSlots.size(); j++) {
                    this.setSlot(i + j, dividerSlots.get(j));
                }
            }

            boolean halfWayThere = i >= (itemContainer.getContainerSize() / 2);
            this.setSlotRedirect(halfWayThere ? i + dividerSlots.size() : i, inventorySlots.apply(i));
        }

        this.setTitle(title);
    }
}
