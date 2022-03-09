package org.samo_lego.simplevillagers.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.samo_lego.simplevillagers.gui.VillagerBlockGui;
import org.samo_lego.simplevillagers.gui.slot.OutputSlot;
import org.samo_lego.simplevillagers.gui.slot.VillagerSlot;

import java.util.List;

import static org.samo_lego.simplevillagers.SimpleVillagers.CONVERTER_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;
import static org.samo_lego.simplevillagers.SimpleVillagers.VILLAGER_ITEM;

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
        return 9;
    }

    @Override
    public void onUse(ServerPlayer player) {
        final ItemStack villagerStack = new ItemStack(VILLAGER_ITEM);
        villagerStack.setHoverName(new TranslatableComponent(EntityType.VILLAGER.getDescriptionId()).append(" ->"));
        villagerStack.enchant(null, 0);

        final ItemStack gappleStack = new ItemStack(Items.GOLDEN_APPLE);
        gappleStack.setHoverName(new TranslatableComponent(gappleStack.getDescriptionId()).append(" ->"));
        gappleStack.enchant(null, 0);


        final ItemStack potionStack = new ItemStack(Items.SPLASH_POTION);
        potionStack.setHoverName(new TranslatableComponent("item.minecraft.potion.effect.weakness").append(" ->"));
        potionStack.enchant(null, 0);

        new VillagerBlockGui(MenuType.GENERIC_9x1, player, this,
                List.of(Pair.of(villagerStack, 1), Pair.of(gappleStack, 1), Pair.of(potionStack, 1), Pair.of(villagerStack, 1)), this::getSlot).open();
    }

    private Slot getSlot(int index) {
        if (index == 0) {
            return new VillagerSlot(this, index);
        } else if (index < 3) {
            return new Slot(this, index, 0, 0);
        }
        return new OutputSlot(this, index);
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
        return 4;
    }
}
