package org.samo_lego.simplevillagers.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.simplevillagers.gui.VillagerBlockGui;
import org.samo_lego.simplevillagers.gui.slot.OutputSlot;
import org.samo_lego.simplevillagers.gui.slot.VillagerSlot;
import org.samo_lego.simplevillagers.util.VillagerUtil;

import java.util.List;
import java.util.Optional;

import static org.samo_lego.simplevillagers.SimpleVillagers.CONFIG;
import static org.samo_lego.simplevillagers.SimpleVillagers.IRON_FARM_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;
import static org.samo_lego.simplevillagers.SimpleVillagers.VILLAGER_ITEM;
import static org.samo_lego.simplevillagers.block.IronFarmBlock.HAS_GOLEM;

public class IronFarmBlockEntity extends AbstractFarmBlockEntity {

    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "iron_farm_block_entity");
    private boolean hasStorageSpace;
    private boolean hasGolem;

    public IronFarmBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(IRON_FARM_BLOCK_ENTITY, blockPos, blockState);
        this.hasStorageSpace = true;
        this.hasGolem = false;
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.simplevillagers.iron_farm");
    }

    @Override
    public int getContainerSize() {
        return 7;  // 3 villager slots + 4 output slots
    }

    @Override
    public void serverTick() {
        // Produce iron & poppies
        // Every 4 minutes => 20 ticks * 60 seconds * 4 minutes = 4800 ticks
        if (this.canOperate() && this.tickCount % CONFIG.golemTimer == 0) {
            this.tickCount = 0;
            this.produceIron();
            this.hasGolem = false;
        } else if (this.canOperate() && this.tickCount % 10 == 0 && this.tickCount >= CONFIG.golemTimer - CONFIG.golemDyingTicks && this.hasStorageSpace) {  // 4800 - 240 = 4560
            this.level.playSound(null, this.getBlockPos(), SoundEvents.IRON_GOLEM_HURT, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (!this.hasGolem) {
                ((ServerLevel) this.level).sendParticles(ParticleTypes.FLAME, this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5, 5, 0, 0, 0, 0.1);
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(HAS_GOLEM, true));
                this.hasGolem = true;
            }
        }
    }

    private void produceIron() {
        ResourceLocation resourceLocation = EntityType.IRON_GOLEM.getDefaultLootTable();
        LootTable lootTable = ((ServerLevel) this.level).getServer().getLootTables().get(resourceLocation);
        LootContext.Builder builder = this.createLootContext();
        lootTable.getRandomItems(builder.create(LootContextParamSets.EMPTY), this::fillIron);
    }

    private void fillIron(ItemStack stack) {
        if (!stack.isEmpty()) {
            Optional<ItemStack> slot = this.items
                    .stream()
                    .filter(stack1 ->
                            stack1.getCount() + stack.getCount() < stack1.getMaxStackSize() && stack1.getItem() == stack.getItem() || stack1.isEmpty())
                    .findFirst();

            this.hasStorageSpace = slot.isPresent();

            if (this.hasStorageSpace && this.hasGolem) {
                final ItemStack itemStack = slot.get();
                if (itemStack.isEmpty()) {
                    this.items.set(this.items.indexOf(itemStack), stack);
                } else {
                    itemStack.grow(stack.getCount());
                }

                this.level.playSound(null, this.getBlockPos(), SoundEvents.IRON_GOLEM_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F);
                this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(HAS_GOLEM, false));

                // Spawn particles
                ((ServerLevel) this.level).sendParticles(ParticleTypes.SMOKE, this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5, 5, 0, 0, 0, 0.1);
                ((ServerLevel) this.level).sendParticles(ParticleTypes.SMALL_FLAME, this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5, 5, 0, 0, 0, 0.1);
            }
        } else {
            this.hasStorageSpace = true;
        }
    }

    protected LootContext.Builder createLootContext() {
        return new LootContext.Builder((ServerLevel) this.level);
    }


    @Override
    public void onUse(ServerPlayer player) {
        final ItemStack left = new ItemStack(VILLAGER_ITEM);
        left.setHoverName(new TranslatableComponent(EntityType.VILLAGER.getDescriptionId()).append(" ->"));
        left.enchant(null, 0);

        final ItemStack right = new ItemStack(Items.IRON_INGOT);
        right.setHoverName(new TranslatableComponent("gamerule.category.drops").append(" ->"));
        right.enchant(null, 0);

        new VillagerBlockGui(MenuType.GENERIC_9x1, player, this, List.of(Pair.of(left, 3), Pair.of(right, 4)), this::getSlot).open();
    }

    @Override
    protected void updateEmptyStatus(int index) {
        if (index < 3) {
            boolean canOperate = true;
            // Check first 3 slots for villagers
            for (int i = 0; i < 3; i++) {
                final ItemStack stack = this.items.get(i);
                canOperate &= stack.getItem() == VILLAGER_ITEM && VillagerUtil.isParent(stack);
            }

            // Update only if the status has changed
            if (canOperate != this.canOperate()) {
                this.setOperative(canOperate);
                super.updateEmptyStatus(index);
            }
        }
    }


    @Override
    public int getScreenSize() {
        return this.getContainerSize();
    }

    private Slot getSlot(int index) {
        return index > 2 ? new OutputSlot(this, index) : new VillagerSlot(this, index);
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.DOWN) {
            // Last four slots are output, so those
            return new int[]{3, 4, 5, 6};
        } else {
            // Only allow input in first 3 slots
            return new int[]{0, 1, 2};
        }
    }
}
