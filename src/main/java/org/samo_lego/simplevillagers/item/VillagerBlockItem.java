package org.samo_lego.simplevillagers.item;

import eu.pb4.polymer.api.client.PolymerClientDecoded;
import eu.pb4.polymer.api.client.PolymerKeepModel;
import eu.pb4.polymer.api.item.PolymerBlockItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import static org.samo_lego.simplevillagers.network.NetworkHandler.isVanilla;

public class VillagerBlockItem extends PolymerBlockItem implements PolymerClientDecoded, PolymerKeepModel{
    public VillagerBlockItem(Block block, Properties settings, Item virtualItem) {
        super(block, settings, virtualItem);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayer player) {
        return isVanilla(player) ? super.getPolymerItem(itemStack, player) : itemStack.getItem();
    }
}
