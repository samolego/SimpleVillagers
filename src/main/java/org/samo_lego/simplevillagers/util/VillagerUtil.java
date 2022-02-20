package org.samo_lego.simplevillagers.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public interface VillagerUtil {
    void forceDefaultTradingScreen(ServerPlayer player);

    static InteractionResult onUseEntity(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (player instanceof ServerPlayer pl && entity instanceof Villager villager && pl.isShiftKeyDown()) {
            // Get the item
            if (villager.isLeashed()) {
                villager.dropLeash(true, true);
            }
            villager.fallDistance = 0;


            //pl.getInventory().add();
        }
        return InteractionResult.PASS;
    }
}
