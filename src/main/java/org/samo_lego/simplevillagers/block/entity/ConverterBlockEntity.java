package org.samo_lego.simplevillagers.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.simplevillagers.gui.VillagerBlockGui;
import org.samo_lego.simplevillagers.gui.slot.OutputSlot;
import org.samo_lego.simplevillagers.gui.slot.VillagerSlot;
import org.samo_lego.simplevillagers.util.VillagerUtil;

import java.util.List;
import java.util.UUID;

import static org.samo_lego.simplevillagers.SimpleVillagers.*;

public class ConverterBlockEntity extends AbstractFarmBlockEntity {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "converter_block_entity");
    private boolean converting;
    private UUID conversionStarter;

    public ConverterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CONVERTER_BLOCK_ENTITY, blockPos, blockState);
        this.converting = false;
        this.conversionStarter = null;
    }

    @Override
    public void serverTick() {
        if (this.canOperate() && !this.converting) {
            this.tickCount = 0;

            this.level.playSound(null, this.getBlockPos(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.HOSTILE, 1.0F, 1.0F);
            ((ServerLevel) this.level).sendParticles(ParticleTypes.ANGRY_VILLAGER, this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5, 1, 0, 0, 0, 0.1);

            // Clear items
            for (int i = 0; i < this.getContainerSize() - 1; i++) {
                this.getItem(i).shrink(1);
            }

            this.converting = true;
        } else if (this.converting && this.tickCount % CONFIG.converter.time == 0) {
            // Conversion has ended
            Villager villager = new Villager(EntityType.VILLAGER, this.level);
            // Loading in the villager data
            villager.load(this.getItem(0).getOrCreateTag());

            if (this.conversionStarter != null) {
                Player player = this.level.getPlayerByUUID(this.conversionStarter);
                if (player != null || !CONFIG.converter.requiresOnlinePlayer) {
                    villager.getGossips().add(this.conversionStarter, GossipType.MAJOR_POSITIVE, 20);
                    villager.getGossips().add(this.conversionStarter, GossipType.MINOR_POSITIVE, 25);
                }
            }

            villager.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));

            // Save villager to tag and create villager item
            CompoundTag villagerTag = new CompoundTag();
            villager.save(villagerTag);
            ItemStack stack = new ItemStack(VILLAGER_ITEM);
            stack.setTag(villagerTag);

            this.setItem(3, stack);

            this.level.levelEvent(null, 1027, this.getBlockPos(), 0);
            ((ServerLevel) this.level).sendParticles(ParticleTypes.HAPPY_VILLAGER, this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5, 1, 0, 0, 0, 0.1);

            this.converting = false;
        }
    }

    @Override
    public int getScreenSize() {
        return 9;
    }

    @Override
    public void onUse(ServerPlayer player) {
        final ItemStack villagerStack = new ItemStack(VILLAGER_ITEM);
        villagerStack.setHoverName(Component.translatable(EntityType.VILLAGER.getDescriptionId()).append(" ->"));
        villagerStack.enchant(null, 0);

        final ItemStack gappleStack = new ItemStack(Items.GOLDEN_APPLE);
        gappleStack.setHoverName(Component.translatable(gappleStack.getDescriptionId()).append(" ->"));
        gappleStack.enchant(null, 0);


        final ItemStack potionStack = new ItemStack(Items.SPLASH_POTION);
        potionStack.setHoverName(Component.translatable("item.minecraft.potion.effect.weakness").append(" ->"));
        potionStack.enchant(null, 0);

        new VillagerBlockGui(MenuType.GENERIC_9x1, player, this,
                List.of(Pair.of(villagerStack, 1), Pair.of(gappleStack, 1), Pair.of(potionStack, 1), Pair.of(villagerStack, 1)),
                this::getSlot).open();

        if (!this.converting)
            this.conversionStarter = player.getUUID();
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        super.setItem(index, stack);
    }

    private Slot getSlot(int index) {
        if (index == 0) {
            return new VillagerSlot(this, index);
        } else if (index < 3) {
            return new Slot(this, index, 0, 0);
        }
        return new OutputSlot(this, index);
    }

    @Override
    protected void updateEmptyStatus(int index) {
        ItemStack gapple = this.getItem(1);
        boolean canOperate = gapple.getItem() == Items.GOLDEN_APPLE;

        if (canOperate) {
            final ItemStack villagerStack = this.getItem(0);
            final ItemStack potionStack = this.getItem(2);

            canOperate = villagerStack.getItem() == VILLAGER_ITEM && (potionStack.getItem() == Items.SPLASH_POTION || CONFIG.converter.requireSplash) &&
                    potionStack.hasTag()&& VillagerUtil.isParent(villagerStack);

            if (canOperate) {
                final Potion potion = BuiltInRegistries.POTION.get(ResourceLocation.tryParse(potionStack.getTag().getString("Potion")));

                canOperate = false;
                for (MobEffectInstance effect: potion.getEffects()) {
                    if (effect.getDescriptionId().equals(MobEffects.WEAKNESS.getDescriptionId())) {
                        canOperate = true;
                        break;
                    }
                }
            }
        }

        // Update only if the status has changed
        if (canOperate != this.canOperate()) {
            this.setOperative(canOperate);
            super.updateEmptyStatus(index);
        }
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.DOWN) {
            return new int[]{3};
        } else if (side == Direction.UP) {
            return new int[]{2};
        } else {
            return new int[]{0};
        }
    }

    @Override
    protected Component getDefaultName() {
        final var name =  Component.translatable("container.simplevillagers.converter");

        if (this.converting) {
                name.append(". ").append(Component.translatable("container.simplevillagers.converter.converting").withStyle(ChatFormatting.ITALIC));
        }
        return name;
    }

    @Override
    public int getContainerSize() {
        return 4;
    }
}
