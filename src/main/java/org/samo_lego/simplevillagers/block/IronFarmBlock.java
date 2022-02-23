package org.samo_lego.simplevillagers.block;

import eu.pb4.polymer.api.block.PolymerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class IronFarmBlock extends Block implements PolymerBlock {
    public IronFarmBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        // Check if BE

        // No villagers inside, return iron block
        return Blocks.IRON_BLOCK;
    }
}

