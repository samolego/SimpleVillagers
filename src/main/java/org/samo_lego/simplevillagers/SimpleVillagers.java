package org.samo_lego.simplevillagers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Material;
import org.samo_lego.simplevillagers.block.IronFarmBlock;
import org.samo_lego.simplevillagers.item.VillagerItem;
import org.samo_lego.simplevillagers.util.VillagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleVillagers implements ModInitializer {
	public static final String MOD_ID = "simplevillagers";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Item VILLAGER_ITEM = new VillagerItem(new FabricItemSettings().group(CreativeModeTab.TAB_MATERIALS).maxCount(1));

	public static final IronFarmBlock EXAMPLE_BLOCK = new IronFarmBlock(FabricBlockSettings.of(Material.BUILDABLE_GLASS).strength(4.0f));

	@Override
	public void onInitialize() {
		LOGGER.info("Loading SimpleVillagers ...");

		UseEntityCallback.EVENT.register(VillagerUtil::onUseEntity);
		Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "villager_item"), VILLAGER_ITEM);
	}
}
