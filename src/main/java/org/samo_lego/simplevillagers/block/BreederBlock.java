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
import org.samo_lego.simplevillagers.block.entity.BreederBlockEntity;

import static org.samo_lego.simplevillagers.SimpleVillagers.BREEDER_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;

public class BreederBlock extends AbstractFarmBlock {

    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "breeder_block");

    public BreederBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        // Check if BE has villagers
        if (state.getBlock() instanceof BreederBlock) {
            if (state.getValue(EMPTY)) {
                // No villagers inside, return glass
                return Blocks.GLASS;
            }
        }
        return Blocks.RED_STAINED_GLASS;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, BREEDER_BLOCK_ENTITY, AbstractFarmBlockEntity::tick);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BreederBlockEntity(pos, state);
    }
}
