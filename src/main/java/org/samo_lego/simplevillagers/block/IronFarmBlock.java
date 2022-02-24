package org.samo_lego.simplevillagers.block;

import eu.pb4.polymer.api.block.PolymerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.samo_lego.simplevillagers.SimpleVillagers.IRON_FARM_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;

@SuppressWarnings({"deprecation"})
public class IronFarmBlock extends BaseEntityBlock implements PolymerBlock, EntityBlock {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "iron_farm_block");

    public IronFarmBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        // Check if BE

        // No villagers inside, return iron block
        return Blocks.IRON_BLOCK;
    }

    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        // With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, IRON_FARM_BLOCK_ENTITY, IronFarmBlockEntity::tick);
    }



    @Override
    public InteractionResult use(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos,
                                 @NotNull Player pl, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!(pl instanceof ServerPlayer player) || hand == InteractionHand.OFF_HAND) {
            return pl.isShiftKeyDown() ? InteractionResult.PASS : InteractionResult.FAIL;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof IronFarmBlockEntity ironFarmBlockEntity) {
            ironFarmBlockEntity.onUse(player);
        }

        return pl.isShiftKeyDown() ? InteractionResult.PASS : InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new IronFarmBlockEntity(pos, state);
    }
}

