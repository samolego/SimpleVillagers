package org.samo_lego.simplevillagers;

import eu.pb4.polymer.api.block.PolymerBlockUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import org.samo_lego.simplevillagers.block.BreederBlock;
import org.samo_lego.simplevillagers.block.ConverterBlock;
import org.samo_lego.simplevillagers.block.IronFarmBlock;
import org.samo_lego.simplevillagers.block.entity.BreederBlockEntity;
import org.samo_lego.simplevillagers.block.entity.ConverterBlockEntity;
import org.samo_lego.simplevillagers.block.entity.IronFarmBlockEntity;
import org.samo_lego.simplevillagers.command.SimpleVillagersCommand;
import org.samo_lego.simplevillagers.item.FarmBlockItem;
import org.samo_lego.simplevillagers.item.VillagerItem;
import org.samo_lego.simplevillagers.util.Config;
import org.samo_lego.simplevillagers.util.VillagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SimpleVillagers implements ModInitializer {
	public static final String MOD_ID = "simplevillagers";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	public static final CreativeModeTab VILLAGER_GROUP = FabricItemGroupBuilder.build(
			new ResourceLocation(MOD_ID, "general"),
			() -> new ItemStack(Items.VILLAGER_SPAWN_EGG));

	public static final Item VILLAGER_ITEM = new VillagerItem(new FabricItemSettings().group(VILLAGER_GROUP).maxCount(1));

	public static final IronFarmBlock IRON_FARM_BLOCK = new IronFarmBlock(FabricBlockSettings.of(Material.GLASS).strength(2.0f).nonOpaque());
	public static BlockEntityType<IronFarmBlockEntity> IRON_FARM_BLOCK_ENTITY;

	public static final BreederBlock BREEDER_BLOCK = new BreederBlock(FabricBlockSettings.of(Material.BUILDABLE_GLASS).strength(2.0f).nonOpaque());
	public static BlockEntityType<BreederBlockEntity> BREEDER_BLOCK_ENTITY;

	public static final ConverterBlock CONVERTER_BLOCK = new ConverterBlock(FabricBlockSettings.of(Material.BUILDABLE_GLASS).strength(2.0f).nonOpaque());
	public static BlockEntityType<ConverterBlockEntity> CONVERTER_BLOCK_ENTITY;

	private static File configFile;
	public static Config CONFIG;

	@Override
	public void onInitialize() {
		LOGGER.info("Loading SimpleVillagers ...");

		configFile = new File(FabricLoader.getInstance().getConfigDir() + "/simplevillagers.json");
		CONFIG = Config.loadConfigFile(configFile);

		UseEntityCallback.EVENT.register(VillagerUtil::onUseEntity);
		Registry.register(Registry.ITEM, VillagerItem.ID, VILLAGER_ITEM);


		Registry.register(Registry.ITEM, IronFarmBlock.ID, new FarmBlockItem(IRON_FARM_BLOCK, new FabricItemSettings().group(VILLAGER_GROUP), Items.WHITE_STAINED_GLASS));
		Registry.register(Registry.BLOCK, IronFarmBlock.ID, IRON_FARM_BLOCK);
		IRON_FARM_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, IronFarmBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(IronFarmBlockEntity::new, IRON_FARM_BLOCK).build(null));


		Registry.register(Registry.ITEM, BreederBlock.ID, new FarmBlockItem(BREEDER_BLOCK, new FabricItemSettings().group(VILLAGER_GROUP), Items.RED_STAINED_GLASS));
		Registry.register(Registry.BLOCK, BreederBlock.ID, BREEDER_BLOCK);
		BREEDER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, BreederBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(BreederBlockEntity::new, BREEDER_BLOCK).build(null));


		Registry.register(Registry.ITEM, ConverterBlock.ID, new FarmBlockItem(CONVERTER_BLOCK, new FabricItemSettings().group(VILLAGER_GROUP), Items.GREEN_STAINED_GLASS));
		Registry.register(Registry.BLOCK, ConverterBlock.ID, CONVERTER_BLOCK);
		CONVERTER_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, ConverterBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(ConverterBlockEntity::new, CONVERTER_BLOCK).build(null));


		PolymerBlockUtils.registerBlockEntity(IRON_FARM_BLOCK_ENTITY, BREEDER_BLOCK_ENTITY, CONVERTER_BLOCK_ENTITY);

		CommandRegistrationCallback.EVENT.register(SimpleVillagersCommand::register);
	}

	public static File getConfigFile() {
		return configFile;
	}
}
