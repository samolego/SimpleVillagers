package org.samo_lego.simplevillagers.util;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
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

    static boolean isParent(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            return tag.getInt("Age") >= 0;
        }
        return true;
    }

    static InteractionResult onUseEntity(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (player instanceof ServerPlayer pl && entity instanceof Villager villager && pl.isShiftKeyDown() && (Permissions.check(pl, "simplevillagers.villager_item.pickup", true) && player.mayBuild())) {
            if (villager.isLeashed()) {
                villager.dropLeash(true, true);
            }

            // Get the villager item
            final ItemStack stack = new ItemStack(VILLAGER_ITEM);

            int emptySlot = pl.getInventory().getFreeSlot();

            if (emptySlot != -1) {
                // Remove the villager
                ((AVillager) villager).callReleaseAllPois();

                saveVillager(villager, stack, true);
                player.getInventory().setItem(emptySlot, stack);
                villager.discard();
            }
        }

        return InteractionResult.PASS;
    }

    static void saveVillager(Villager villager, ItemStack stack, boolean removeJobSite) {
        final CompoundTag villagerTag = new CompoundTag();
        if (villager.getVillagerXp() > 0 || villager.isBaby()) {
            villager.saveWithoutId(villagerTag);
            final CompoundTag brain = villagerTag.getCompound("Brain");
            if (!brain.isEmpty() && removeJobSite) {
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

            if (villager.isBaby()) {
                final CompoundTag loreTag = new CompoundTag();
                final ListTag nbtLore = new ListTag();
                nbtLore.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Baby"))));
                loreTag.put(ItemStack.TAG_LORE, nbtLore);

                villagerTag.put(ItemStack.TAG_DISPLAY, loreTag);
            }

            stack.setTag(villagerTag);
        }
    }
}
