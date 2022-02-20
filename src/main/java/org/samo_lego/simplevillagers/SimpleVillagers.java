package org.samo_lego.simplevillagers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.samo_lego.simplevillagers.util.VillagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleVillagers implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("simplevillagers");
	public static final Item VILLAGER_ITEM = new Item(new FabricItemSettings().group(CreativeModeTab.TAB_MATERIALS));

	@Override
	public void onInitialize() {
		LOGGER.info("Loading simplevillagers...");

		UseEntityCallback.EVENT.register(VillagerUtil::onUseEntity);
	}
}
