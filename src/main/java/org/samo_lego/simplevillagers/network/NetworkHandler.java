package org.samo_lego.simplevillagers.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;

/**
 * Inspired by <a href="https://github.com/QuiltServerTools/Interdimensional/blob/master/portals-api/src/main/java/net/quiltservertools/interdimensional/portals/networking/NetworkManager.java">Interdimensional</a>
 */
public class NetworkHandler {
    public static final ResourceLocation SV_HELLO = new ResourceLocation(MOD_ID, "hello");

    public static boolean isVanilla(@Nullable ServerPlayer player) {
        return player == null || !ServerPlayNetworking.canSend(player, SV_HELLO);
    }
}
