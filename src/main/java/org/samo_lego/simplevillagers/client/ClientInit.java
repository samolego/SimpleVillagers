package org.samo_lego.simplevillagers.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.samo_lego.simplevillagers.network.NetworkHandler;

public class ClientInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.SV_HELLO, (client, handler, buf, responseSender) -> {});
    }
}
