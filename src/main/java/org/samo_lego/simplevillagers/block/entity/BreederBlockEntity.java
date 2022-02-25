package org.samo_lego.simplevillagers.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.simplevillagers.gui.NoPutSlot;
import org.samo_lego.simplevillagers.gui.VillagerBlockGui;
import org.samo_lego.simplevillagers.util.VillagerUtil;

import java.util.List;

import static net.minecraft.world.entity.npc.Villager.FOOD_POINTS;
import static org.samo_lego.simplevillagers.SimpleVillagers.BREEDER_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;
import static org.samo_lego.simplevillagers.SimpleVillagers.VILLAGER_ITEM;

public class BreederBlockEntity extends AbstractFarmBlockEntity {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "breeder_block_entity");
    private boolean foodReserves;

    public BreederBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BREEDER_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    public void serverTick() {
        // Baby villagers
        // Every 4 minutes => 20 ticks * 60 seconds * 4 minutes = 4800 ticks
        if (this.canOperate() && this.foodReserves && this.tickCount % 100 == 0) {  // todo 4800
            this.tickCount = 0;

            // Decrease food reserves
            this.items.stream().filter(stack -> FOOD_POINTS.containsKey(stack.getItem())).findFirst().ifPresent(stack -> {
                stack.shrink(FOOD_POINTS.get(stack.getItem()));
            });
            this.foodReserves = this.countFood() > 0;
            System.out.println("Food reserves: " + this.foodReserves + " (" + this.countFood() + ")");

            int size = this.getItems().size();
            for (int i = size - 4; i < size; i++) {
                if (this.items.get(i).isEmpty()) {
                    final ItemStack babyVillager = new ItemStack(VILLAGER_ITEM);
                    final CompoundTag babyTag = new CompoundTag();
                    babyTag.putInt("Age", -24000);
                    babyVillager.setTag(babyTag);

                    this.items.set(i, babyVillager);
                    break;
                }
            }
        }
    }

    private int countFood() {
        return FOOD_POINTS.entrySet().stream().mapToInt(entry -> this.countItem(entry.getKey()) / entry.getValue()).sum();
    }

    @Override
    public void onUse(ServerPlayer player) {
        final ItemStack villagerStack = new ItemStack(VILLAGER_ITEM);
        villagerStack.setHoverName(new TranslatableComponent(EntityType.VILLAGER.getDescriptionId()).append(" ->"));
        villagerStack.enchant(null, 0);

        final ItemStack foodStack = new ItemStack(Items.CARROT);
        foodStack.setHoverName(new TranslatableComponent("itemGroup.food").append(" ->"));
        foodStack.enchant(null, 0);

        new VillagerBlockGui(MenuType.GENERIC_9x2, player, this,
                List.of(Pair.of(villagerStack, 2), Pair.of(foodStack, 4), Pair.of(villagerStack, 4)), this::getSlot).open();
    }

    private Slot getSlot(int index) {
        return index > 5 ? new NoPutSlot(this, index) : new Slot(this, index, 0, 0);
    }


    @Override
    protected void updateEmptyStatus(int index) {
        if (index < 2) {
            boolean canOperate = true;
            // Check first 2 slots for villagers
            for (int i = 0; i < 2; i++) {
                final ItemStack stack = this.items.get(i);
                canOperate &= stack.getItem() == VILLAGER_ITEM && !VillagerUtil.isBaby(stack);
            }

            // Update only if the status has changed
            if (canOperate != this.canOperate()) {
                this.setOperative(canOperate);
                super.updateEmptyStatus(index);
            }
        } else if (index < 7) {  // Check for food (index between 2 and 6)
            this.foodReserves = this.countFood() > 0;
        }
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.simplevillagers.breeder");
    }

    @Override
    public int getContainerSize() {
        return 10;
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.DOWN) {
            // Last four slots are output, so those
            return new int[]{6, 7, 8, 9};
        } else {
            // Only allow input in first 3 slots
            return new int[]{0, 1, 2, 3, 4, 5};
        }
    }
}
