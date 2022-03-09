package org.samo_lego.simplevillagers.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import static org.samo_lego.simplevillagers.SimpleVillagers.CONVERTER_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;

public class ConverterBlockEntity extends AbstractFarmBlockEntity {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "converter_block_entity");

    public ConverterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CONVERTER_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    public void serverTick() {

    }

    @Override
    public int getScreenSize() {
        return 0;
    }

    @Override
    public void onUse(ServerPlayer player) {

    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    protected Component getDefaultName() {
        return null;
    }

    @Override
    public int getContainerSize() {
        return 0;
    }
}
