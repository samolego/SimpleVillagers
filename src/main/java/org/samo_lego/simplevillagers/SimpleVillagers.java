package org.samo_lego.simplevillagers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.samo_lego.simplevillagers.item.VillagerItem;
import org.samo_lego.simplevillagers.util.VillagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleVillagers implements ModInitializer {
	public static final String MOD_ID = "simplevillagers";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Item VILLAGER_ITEM = new VillagerItem(new FabricItemSettings().group(CreativeModeTab.TAB_MATERIALS).maxCount(1));

	@Override
	public void onInitialize() {
		LOGGER.info("Loading SimpleVillagers ...");

		UseEntityCallback.EVENT.register(VillagerUtil::onUseEntity);
		Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID, "villager_item"), VILLAGER_ITEM);
	}
}
