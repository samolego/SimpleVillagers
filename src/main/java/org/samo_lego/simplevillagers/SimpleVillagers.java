package org.samo_lego.simplevillagers;

import eu.pb4.polymer.api.block.PolymerBlockUtils;
import eu.pb4.polymer.api.item.PolymerBlockItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import org.samo_lego.simplevillagers.block.BreederBlock;
import org.samo_lego.simplevillagers.block.IronFarmBlock;
import org.samo_lego.simplevillagers.block.entity.BreederBlockEntity;
import org.samo_lego.simplevillagers.block.entity.IronFarmBlockEntity;
import org.samo_lego.simplevillagers.item.VillagerItem;
import org.samo_lego.simplevillagers.util.VillagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleVillagers implements ModInitializer {
	public static final String MOD_ID = "simplevillagers";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Item VILLAGER_ITEM = new VillagerItem(new FabricItemSettings().group(CreativeModeTab.TAB_MATERIALS).maxCount(1));

	public static final IronFarmBlock IRON_FARM_BLOCK = new IronFarmBlock(FabricBlockSettings.of(Material.BUILDABLE_GLASS).strength(4.0f).nonOpaque());
	public static BlockEntityType<IronFarmBlockEntity> IRON_FARM_BLOCK_ENTITY;

	public static final BreederBlock BREEDER_BLOCK = new BreederBlock(FabricBlockSettings.of(Material.BUILDABLE_GLASS).strength(4.0f).nonOpaque());
	public static BlockEntityType<BreederBlockEntity> BREEDER_BLOCK_ENTITY;

	@Override
	public void onInitialize() {
		LOGGER.info("Loading SimpleVillagers ...");

		UseEntityCallback.EVENT.register(VillagerUtil::onUseEntity);
		Registry.register(Registry.ITEM, VillagerItem.ID, VILLAGER_ITEM);

		Registry.register(Registry.ITEM, IronFarmBlock.ID, new PolymerBlockItem(IRON_FARM_BLOCK, new FabricItemSettings().group(CreativeModeTab.TAB_DECORATIONS), Items.GLASS));
		Registry.register(Registry.BLOCK, IronFarmBlock.ID, IRON_FARM_BLOCK);

		IRON_FARM_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, IronFarmBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(IronFarmBlockEntity::new, IRON_FARM_BLOCK).build(null));


		Registry.register(Registry.ITEM, BreederBlock.ID, new PolymerBlockItem(BREEDER_BLOCK, new FabricItemSettings().group(CreativeModeTab.TAB_DECORATIONS), Items.GLASS));
		Registry.register(Registry.BLOCK, BreederBlock.ID, BREEDER_BLOCK);

		BREEDER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, BreederBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(BreederBlockEntity::new, BREEDER_BLOCK).build(null));

		PolymerBlockUtils.registerBlockEntity(IRON_FARM_BLOCK_ENTITY, BREEDER_BLOCK_ENTITY);
	}
}
