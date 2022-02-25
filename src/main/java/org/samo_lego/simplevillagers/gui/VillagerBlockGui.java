package org.samo_lego.simplevillagers.gui;

import com.mojang.datafixers.util.Pair;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.samo_lego.simplevillagers.block.entity.AbstractFarmBlockEntity;

import java.util.List;
import java.util.function.Function;

public class VillagerBlockGui extends SimpleGui {

    public VillagerBlockGui(MenuType<?> type, ServerPlayer player, AbstractFarmBlockEntity itemContainer,
                            List<Pair<ItemStack, Integer>> dividerSlots, Function<Integer, Slot> slotDeterminer) {
        super(type, player, false);

        int count = 0;
        for (int i = 0; i < dividerSlots.size(); i++) {
            Pair<ItemStack, Integer> pair = dividerSlots.get(i);

            this.setSlot(i + count, pair.getFirst());  // Divider slot

            for (int j = 0; j < pair.getSecond(); j++) {
                this.setSlotRedirect(i + count + j + 1, slotDeterminer.apply(count + j));  // Slot for each item
            }

            count += pair.getSecond();
        }

        final ItemStack emptySlot = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        if (count < itemContainer.getContainerSize()) {
            for (int i = 0; i < itemContainer.getContainerSize() - count; i++) {
                this.setSlot(i + count, emptySlot);  // Empty slots
            }
        }

        this.setTitle(itemContainer.getDisplayName());
    }
}
