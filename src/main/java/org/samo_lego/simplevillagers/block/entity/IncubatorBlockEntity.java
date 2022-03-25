package org.samo_lego.simplevillagers.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.simplevillagers.gui.VillagerBlockGui;
import org.samo_lego.simplevillagers.gui.slot.VillagerSlot;
import org.samo_lego.simplevillagers.util.VillagerUtil;

import java.util.ArrayList;
import java.util.List;

import static org.samo_lego.simplevillagers.SimpleVillagers.CONFIG;
import static org.samo_lego.simplevillagers.SimpleVillagers.INCUBATOR_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;
import static org.samo_lego.simplevillagers.SimpleVillagers.VILLAGER_ITEM;

public class IncubatorBlockEntity extends AbstractFarmBlockEntity {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "incubator_block_entity");

    public IncubatorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(INCUBATOR_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    public void serverTick() {
        // Baby villagers
        if (this.canOperate())
            this.growBabies(0, CONFIG.babyAgeIncrease);
    }

    @Override
    public int getScreenSize() {
        return 9;  // 1 row * 9 slots
    }

    @Override
    public void onUse(ServerPlayer player) {
        final ItemStack villagerStack = new ItemStack(VILLAGER_ITEM);
        villagerStack.setHoverName(new TranslatableComponent(EntityType.VILLAGER.getDescriptionId()).append(" ->"));
        villagerStack.enchant(null, 0);

        new VillagerBlockGui(MenuType.GENERIC_9x1, player, this,
                List.of(Pair.of(villagerStack, 8)), this::getSlot).open();
    }

    private Slot getSlot(int index) {
        return new VillagerSlot(this, index);
    }


    @Override
    protected void updateEmptyStatus(int index) {
        boolean canOperate = false;

        // Check first 2 slots for villagers
        for (int i = 0; i < this.getContainerSize(); ++i) {
            final ItemStack stack = this.items.get(i);

            // Found at least one baby villager
            if (stack.getItem() == VILLAGER_ITEM && !VillagerUtil.isParent(stack)) {
                canOperate = true;
                break;
            }
        }

        // Update only if the status has changed
        if (canOperate != this.canOperate()) {
            this.setOperative(canOperate);
            super.updateEmptyStatus(index);
        }
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.simplevillagers.incubator");
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.DOWN) {
            // Determine which slots have adult villagers
            final List<Integer> slots = new ArrayList<>();

            for (int i = 0; i < this.getContainerSize(); ++i) {
                final ItemStack stack = this.items.get(i);
                if (stack.getItem() == VILLAGER_ITEM && VillagerUtil.isParent(stack)) {
                    slots.add(i);
                }
            }

            return slots.stream().mapToInt(Integer::intValue).toArray();
        }
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    }
}
