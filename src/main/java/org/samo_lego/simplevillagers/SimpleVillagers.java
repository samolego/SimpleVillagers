package org.samo_lego.simplevillagers;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import org.samo_lego.simplevillagers.block.AbstractFarmBlock;
import org.samo_lego.simplevillagers.block.BreederBlock;
import org.samo_lego.simplevillagers.block.ConverterBlock;
import org.samo_lego.simplevillagers.block.IncubatorBlock;
import org.samo_lego.simplevillagers.block.IronFarmBlock;
import org.samo_lego.simplevillagers.block.TradingBlock;
import org.samo_lego.simplevillagers.block.entity.BreederBlockEntity;
import org.samo_lego.simplevillagers.block.entity.ConverterBlockEntity;
import org.samo_lego.simplevillagers.block.entity.IncubatorBlockEntity;
import org.samo_lego.simplevillagers.block.entity.IronFarmBlockEntity;
import org.samo_lego.simplevillagers.block.entity.TradingBlockEntity;
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


	public static final CreativeModeTab VILLAGER_GROUP;

	public static final Item VILLAGER_ITEM;

	public static final IronFarmBlock IRON_FARM_BLOCK;

	public static BlockEntityType<IronFarmBlockEntity> IRON_FARM_BLOCK_ENTITY;

	public static final BreederBlock BREEDER_BLOCK;

	public static BlockEntityType<BreederBlockEntity> BREEDER_BLOCK_ENTITY;

	public static final ConverterBlock CONVERTER_BLOCK;

	public static BlockEntityType<ConverterBlockEntity> CONVERTER_BLOCK_ENTITY;

	public static final IncubatorBlock INCUBATOR_BLOCK;

	public static BlockEntityType<IncubatorBlockEntity> INCUBATOR_BLOCK_ENTITY;

	public static final TradingBlock TRADING_BLOCK;

	public static BlockEntityType<TradingBlockEntity> TRADING_BLOCK_ENTITY;

	private static File configFile;
	public static Config CONFIG;

	static {
		VILLAGER_GROUP = FabricItemGroup.builder(
						new ResourceLocation(MOD_ID, "general"))
				.icon(() -> new ItemStack(Items.VILLAGER_SPAWN_EGG))
				.build();
		VILLAGER_ITEM = new VillagerItem(new FabricItemSettings().maxCount(1));
		IRON_FARM_BLOCK = new IronFarmBlock(FabricBlockSettings.of(Material.GLASS).strength(2.0f).nonOpaque());
		BREEDER_BLOCK = new BreederBlock(FabricBlockSettings.of(Material.BUILDABLE_GLASS).strength(2.0f).nonOpaque());
		CONVERTER_BLOCK = new ConverterBlock(FabricBlockSettings.of(Material.BUILDABLE_GLASS).strength(2.0f).nonOpaque());
		INCUBATOR_BLOCK = new IncubatorBlock(FabricBlockSettings.of(Material.BUILDABLE_GLASS).strength(2.0f).nonOpaque());
		TRADING_BLOCK = new TradingBlock(FabricBlockSettings.of(Material.BUILDABLE_GLASS).strength(2.0f).nonOpaque());


		ItemGroupEvents.modifyEntriesEvent(VILLAGER_GROUP).register(content -> {
			content.accept(VILLAGER_ITEM);
			content.accept(IRON_FARM_BLOCK.asItem());
			content.accept(BREEDER_BLOCK.asItem());
			content.accept(CONVERTER_BLOCK.asItem());
			content.accept(INCUBATOR_BLOCK.asItem());
			// todo content.accept(TRADING_BLOCK.asItem());
		});
	}

	public static File getConfigFile() {
		return configFile;
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Hrmpf! Loading SimpleVillagers ...");

		configFile = new File(FabricLoader.getInstance().getConfigDir() + "/simplevillagers.json");
		CONFIG = Config.loadConfigFile(configFile);

		UseEntityCallback.EVENT.register(VillagerUtil::onUseEntity);
		PlayerBlockBreakEvents.BEFORE.register(AbstractFarmBlock::onDestroy);
		Registry.register(BuiltInRegistries.ITEM, VillagerItem.ID, VILLAGER_ITEM);


		Registry.register(BuiltInRegistries.ITEM, IronFarmBlock.ID, new FarmBlockItem(IRON_FARM_BLOCK, new FabricItemSettings(), Items.WHITE_STAINED_GLASS));
		Registry.register(BuiltInRegistries.BLOCK, IronFarmBlock.ID, IRON_FARM_BLOCK);
		IRON_FARM_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, IronFarmBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(IronFarmBlockEntity::new, IRON_FARM_BLOCK).build(null));


		Registry.register(BuiltInRegistries.ITEM, BreederBlock.ID, new FarmBlockItem(BREEDER_BLOCK, new FabricItemSettings(), Items.RED_STAINED_GLASS));
		Registry.register(BuiltInRegistries.BLOCK, BreederBlock.ID, BREEDER_BLOCK);
		BREEDER_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, BreederBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(BreederBlockEntity::new, BREEDER_BLOCK).build(null));


		Registry.register(BuiltInRegistries.ITEM, ConverterBlock.ID, new FarmBlockItem(CONVERTER_BLOCK, new FabricItemSettings(), Items.GREEN_STAINED_GLASS));
		Registry.register(BuiltInRegistries.BLOCK, ConverterBlock.ID, CONVERTER_BLOCK);
		CONVERTER_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ConverterBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(ConverterBlockEntity::new, CONVERTER_BLOCK).build(null));


		Registry.register(BuiltInRegistries.ITEM, IncubatorBlock.ID, new FarmBlockItem(INCUBATOR_BLOCK, new FabricItemSettings(), Items.CYAN_STAINED_GLASS));
		Registry.register(BuiltInRegistries.BLOCK, IncubatorBlock.ID, INCUBATOR_BLOCK);
		INCUBATOR_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, IncubatorBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(IncubatorBlockEntity::new, INCUBATOR_BLOCK).build(null));


		Registry.register(BuiltInRegistries.ITEM, TradingBlock.ID, new FarmBlockItem(TRADING_BLOCK, new FabricItemSettings(), Items.ORANGE_STAINED_GLASS));
		Registry.register(BuiltInRegistries.BLOCK, TradingBlock.ID, TRADING_BLOCK);
		TRADING_BLOCK_ENTITY = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, TradingBlockEntity.ID,
				FabricBlockEntityTypeBuilder.create(TradingBlockEntity::new, TRADING_BLOCK).build(null));


		PolymerBlockUtils.registerBlockEntity(IRON_FARM_BLOCK_ENTITY,
				BREEDER_BLOCK_ENTITY,
				CONVERTER_BLOCK_ENTITY,
				INCUBATOR_BLOCK_ENTITY,
				TRADING_BLOCK_ENTITY);

		CommandRegistrationCallback.EVENT.register(SimpleVillagersCommand::register);
	}
}
