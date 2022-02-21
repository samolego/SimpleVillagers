package org.samo_lego.simplevillagers.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.simplevillagers.mixin.AVillager;

import static org.samo_lego.simplevillagers.SimpleVillagers.VILLAGER_ITEM;

public interface VillagerUtil {
    void forceDefaultTradingScreen(boolean force);

    static InteractionResult onUseEntity(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (player instanceof ServerPlayer pl && entity instanceof Villager villager && pl.isShiftKeyDown() && Permissions.check(pl, "simplevillagers.villager_item.pickup", true)) {
            // Get the villager item
            if (villager.isLeashed()) {
                villager.dropLeash(true, true);
            }

            final ItemStack stack = new ItemStack(VILLAGER_ITEM);
            final CompoundTag villagerTag = new CompoundTag();

            boolean added = false;
            if (pl.getMainHandItem().isEmpty()) {
                pl.setItemInHand(InteractionHand.MAIN_HAND, stack);
                added = true;
            } else if (pl.getInventory().add(stack)) {
                added = true;
            }

            if (added) {
                // Remove the villager
                ((AVillager) villager).callReleaseAllPois();

                villager.saveWithoutId(villagerTag);
                final CompoundTag brain = villagerTag.getCompound("Brain");
                if (!brain.isEmpty()) {
                    final CompoundTag memories = brain.getCompound("memories");

                    if (!memories.isEmpty()) {
                        memories.remove("minecraft:job_site");
                    }
                }

                villagerTag.remove("Pos");
                villagerTag.remove("Motion");
                villagerTag.remove("Rotation");
                villagerTag.remove("UUID");
                villagerTag.remove("Dimension");

                stack.setTag(villagerTag);

                villager.discard();
            }
        }

        return InteractionResult.PASS;
    }
}
