package org.samo_lego.simplevillagers.item;

import eu.pb4.polymer.api.client.PolymerClientDecoded;
import eu.pb4.polymer.api.client.PolymerKeepModel;
import eu.pb4.polymer.api.item.PolymerBlockItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import static org.samo_lego.simplevillagers.SimpleVillagers.IRON_FARM_BLOCK;
import static org.samo_lego.simplevillagers.network.NetworkHandler.isVanilla;

public class IronFarmBlockItem extends PolymerBlockItem implements PolymerClientDecoded, PolymerKeepModel {
    public IronFarmBlockItem(Properties settings) {
        super(IRON_FARM_BLOCK, settings, Items.WHITE_STAINED_GLASS);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayer player) {
        return isVanilla(player) ? super.getPolymerItem(itemStack, player) : this;
    }
}
