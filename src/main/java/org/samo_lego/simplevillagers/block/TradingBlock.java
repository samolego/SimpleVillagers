package org.samo_lego.simplevillagers.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.simplevillagers.block.entity.AbstractFarmBlockEntity;
import org.samo_lego.simplevillagers.block.entity.TradingBlockEntity;

import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;
import static org.samo_lego.simplevillagers.SimpleVillagers.TRADING_BLOCK_ENTITY;

public class TradingBlock extends AbstractFarmBlock {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "trading_block");

    public TradingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        if (state.getValue(EMPTY)) {
            return Blocks.GLASS;
        }

        return Blocks.ORANGE_STAINED_GLASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TradingBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, TRADING_BLOCK_ENTITY, AbstractFarmBlockEntity::tick);
    }
}
