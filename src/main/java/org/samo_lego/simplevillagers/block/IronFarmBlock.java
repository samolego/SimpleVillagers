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

import static org.samo_lego.simplevillagers.SimpleVillagers.IRON_FARM_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;

public class IronFarmBlock extends AbstractFarmBlock {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "iron_farm_block");

    public IronFarmBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        // Check if BE has villagers
        if (state.getBlock() instanceof IronFarmBlock) {
            if (state.getValue(EMPTY)) {
                // No villagers inside, return glass
                return Blocks.WHITE_STAINED_GLASS;
            }
        }
        return Blocks.IRON_BLOCK;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, IRON_FARM_BLOCK_ENTITY, AbstractFarmBlockEntity::tick);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new IronFarmBlockEntity(pos, state);
    }
}

