package org.samo_lego.simplevillagers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.simplevillagers.gui.NoPutSlot;
import org.samo_lego.simplevillagers.gui.VillagerBlockGui;

import java.util.List;

import static org.samo_lego.simplevillagers.SimpleVillagers.IRON_FARM_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;
import static org.samo_lego.simplevillagers.SimpleVillagers.VILLAGER_ITEM;

public class IronFarmBlockEntity extends AbstractFarmBlockEntity {

    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "iron_farm_block_entity");

    public IronFarmBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(IRON_FARM_BLOCK_ENTITY, blockPos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, IronFarmBlockEntity be) {
        if (!level.isClientSide)
            be.serverTick();
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.simplevillagers.iron_farm");
    }

    @Override
    public int getContainerSize() {
        return 7;
    }

    @Override
    public void serverTick() {
        // Produce iron & poppies
        // Every 4 minutes (20 ticks * 60 seconds * 4 minutes)
        if (this.canOperate() && this.tickCount % 20 == 0) {  // todo 4800
            this.tickCount = 0;
            this.produceIron();
        }
    }

    @Override
    public boolean canOperate() {
        return this.items.stream().filter(stack -> stack.getItem() == VILLAGER_ITEM).count() > 2;
    }

    private void produceIron() {
        ResourceLocation resourceLocation = EntityType.IRON_GOLEM.getDefaultLootTable();
        LootTable lootTable = ((ServerLevel) this.level).getServer().getLootTables().get(resourceLocation);
        LootContext.Builder builder = this.createLootContext();
        lootTable.getRandomItems(builder.create(LootContextParamSets.EMPTY), this::fillIron);
    }

    private void fillIron(ItemStack stack) {
        if (!stack.isEmpty()) {
            this.items.stream()
                    .filter(stack1 ->
                            stack1.getCount() + stack.getCount() < stack1.getMaxStackSize() && stack1.getItem() == stack.getItem() || stack1.isEmpty())
                    .findFirst()
                    .ifPresent(itemStack -> {
                        if (itemStack.isEmpty()) {
                            this.items.set(this.items.indexOf(itemStack), stack);
                        } else {
                            itemStack.grow(stack.getCount());
                        }
                    });
        }
    }

    protected LootContext.Builder createLootContext() {
        return new LootContext.Builder((ServerLevel) this.level);
    }

    @Override
    public void onUse(ServerPlayer player) {
        final ItemStack left = new ItemStack(VILLAGER_ITEM);
        left.setHoverName(new TextComponent("<- ").append(new TranslatableComponent(EntityType.VILLAGER.getDescriptionId())));
        left.enchant(null, 0);

        final ItemStack right = new ItemStack(Items.IRON_INGOT);
        right.setHoverName(new TranslatableComponent("gamerule.category.drops").append(" ->"));
        right.enchant(null, 0);

        new VillagerBlockGui(player, this, this.getDefaultName(), List.of(left, right), this::getSlot).open();
    }

    private Slot getSlot(int index) {
        return index > 2 ? new NoPutSlot(this, index) : new Slot(this, index, 0, 0);
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
