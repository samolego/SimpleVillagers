package org.samo_lego.simplevillagers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.samo_lego.simplevillagers.block.AbstractFarmBlock.EMPTY;

public abstract class AbstractFarmBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    protected NonNullList<ItemStack> items;
    protected int tickCount;
    private boolean canOperate;

    protected AbstractFarmBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack itemStack, @Nullable Direction direction) {
        return direction != Direction.DOWN;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        return direction == Direction.DOWN;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return this.getItems().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int index) {
        return this.getItems().get(index);
    }

    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        final ItemStack itemStack = ContainerHelper.removeItem(this.getItems(), index, count);
        if (!itemStack.isEmpty()) {
            this.setChanged();
        }
        this.updateEmptyStatus(index);
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(this.getItems(), index);
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.getItems().set(index, stack);
        this.updateEmptyStatus(index);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr((double) this.worldPosition.getX() + 0.5, (double) this.worldPosition.getY() + 0.5, (double) this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    public abstract void serverTick();

    public boolean canOperate() {
        return this.canOperate;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        final CompoundTag items = new CompoundTag();
        ContainerHelper.saveAllItems(items, this.getItems());
        tag.put("Items", items);
        tag.putInt("TickCount", this.tickCount);
        tag.putBoolean("Operative", this.canOperate);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        final CompoundTag items = tag.getCompound("Items");
        ContainerHelper.loadAllItems(items, this.items);

        this.tickCount = tag.getInt("TickCount");
        this.setOperative(tag.getBoolean("Operative"));


        if (this.level != null) {
            this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(EMPTY, !this.canOperate()));
        }
    }

    @Override
    public void clearContent() {
        this.getItems().clear();
    }

    public abstract void onUse(ServerPlayer player);

    public static void tick(Level level, BlockPos pos, BlockState state, AbstractFarmBlockEntity be) {
        if (!level.isClientSide) {
            ++be.tickCount;
            be.serverTick();
        }
    }

    protected void updateEmptyStatus(int index) {
        this.level.setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(EMPTY, !this.canOperate()));
    }

    protected void setOperative(boolean operative) {
        this.canOperate = operative;
    }
}
