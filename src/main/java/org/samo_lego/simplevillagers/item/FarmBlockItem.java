package org.samo_lego.simplevillagers.item;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import static org.samo_lego.simplevillagers.network.NetworkHandler.isVanilla;

/**
 * Item for the basic (abstract) farm block.
 */
public class FarmBlockItem extends PolymerBlockItem implements PolymerClientDecoded, PolymerKeepModel {
    public FarmBlockItem(Block block, Properties settings, Item virtualItem) {
        super(block, settings, virtualItem);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayer player) {
        return isVanilla(player) ? super.getPolymerItem(itemStack, player) : itemStack.getItem();
    }
}
