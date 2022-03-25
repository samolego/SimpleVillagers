package org.samo_lego.simplevillagers.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.RenderType;
import org.samo_lego.simplevillagers.network.NetworkHandler;

import static org.samo_lego.simplevillagers.SimpleVillagers.BREEDER_BLOCK;
import static org.samo_lego.simplevillagers.SimpleVillagers.CONVERTER_BLOCK;
import static org.samo_lego.simplevillagers.SimpleVillagers.INCUBATOR_BLOCK;
import static org.samo_lego.simplevillagers.SimpleVillagers.IRON_FARM_BLOCK;

public class ClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.SV_HELLO, (client, handler, buf, responseSender) -> {});
        BlockRenderLayerMap.INSTANCE.putBlock(IRON_FARM_BLOCK, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BREEDER_BLOCK, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(CONVERTER_BLOCK, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(INCUBATOR_BLOCK, RenderType.cutout());
    }
}
