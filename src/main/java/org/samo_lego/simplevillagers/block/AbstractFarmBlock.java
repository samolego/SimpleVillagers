package org.samo_lego.simplevillagers.block;

import eu.pb4.polymer.api.block.PolymerBlock;
import eu.pb4.polymer.api.client.PolymerClientDecoded;
import eu.pb4.polymer.api.client.PolymerKeepModel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.simplevillagers.block.entity.AbstractFarmBlockEntity;

@SuppressWarnings({"deprecation"})
public abstract class AbstractFarmBlock extends BaseEntityBlock implements PolymerBlock, PolymerClientDecoded, PolymerKeepModel, EntityBlock {
    public static final BooleanProperty EMPTY = BooleanProperty.create("empty");

    public AbstractFarmBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(EMPTY, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EMPTY);
    }

    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, Level level, @NotNull BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos,
                                 @NotNull Player pl, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!(pl instanceof ServerPlayer player) || hand == InteractionHand.OFF_HAND) {
            return pl.isShiftKeyDown() ? InteractionResult.PASS : InteractionResult.FAIL;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof AbstractFarmBlockEntity farmBlockEntity) {
            farmBlockEntity.onUse(player);
        }

        return pl.isShiftKeyDown() ? InteractionResult.PASS : InteractionResult.SUCCESS;
    }
}

