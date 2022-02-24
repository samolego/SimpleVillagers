package org.samo_lego.simplevillagers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.simplevillagers.gui.VillagerBlockGui;

import java.util.Random;

import static org.samo_lego.simplevillagers.SimpleVillagers.IRON_FARM_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;
import static org.samo_lego.simplevillagers.SimpleVillagers.VILLAGER_ITEM;

public class IronFarmBlockEntity extends BaseContainerBlockEntity {

    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "iron_farm_block_entity");
    private final NonNullList<ItemStack> items = NonNullList.withSize(7, ItemStack.EMPTY);
    private int tickCount;
    private final Random random;

    public IronFarmBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(IRON_FARM_BLOCK_ENTITY, blockPos, blockState);
        this.random = new Random();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, IronFarmBlockEntity be) {
        if (!level.isClientSide)
            be.serverTick();
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("simplevillagers.container.iron_farm");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        return ChestMenu.oneRow(containerId, inventory);
    }

    @Override
    public int getContainerSize() {
        return 7;
    }

    @Override
    public boolean isEmpty() {
        return this.getItems().stream().allMatch(ItemStack::isEmpty);
    }

    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        final ItemStack itemStack = ContainerHelper.removeItem(this.getItems(), index, count);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.getItems(), index);
    }

    @Override
    public void setItem(int index, @NotNull ItemStack stack) {
        this.items.set(index, stack);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr((double) this.worldPosition.getX() + 0.5, (double) this.worldPosition.getY() + 0.5, (double) this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void serverTick() {
        // Produce iron & poppies
        // Every 4 minutes (20 ticks * 60 seconds * 4 minutes)
        if (this.hasVillagers() && ++this.tickCount % 20 == 0) {  // todo 4800
            this.tickCount = 0;
            this.produceIron();
        }
    }

    private boolean hasVillagers() {
        return this.items.stream().filter(stack -> stack.getItem() == VILLAGER_ITEM).count() > 2;
    }

    private void produceIron() {
        ResourceLocation resourceLocation = EntityType.IRON_GOLEM.getDefaultLootTable();
        LootTable lootTable = ((ServerLevel) this.level).getServer().getLootTables().get(resourceLocation);
        LootContext.Builder builder = this.createLootContext();
        lootTable.getRandomItems(builder.create(LootContextParamSets.EMPTY), this::fillIron);
        //lootTable.fill(this, builder.create(LootContextParamSets.ALL_PARAMS), this::fillIron);
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
    public boolean hasCustomName() {
        return super.hasCustomName();
    }

    public void onUse(ServerPlayer player) {
        new VillagerBlockGui(player, this, 2).open();
    }
}
