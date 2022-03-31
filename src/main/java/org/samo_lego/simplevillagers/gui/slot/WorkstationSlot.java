package org.samo_lego.simplevillagers.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

import static net.minecraft.world.entity.ai.village.poi.PoiType.ALL_JOBS;

public class WorkstationSlot extends Slot {
    public WorkstationSlot(Container container, int i) {
        super(container, i, 0, 0);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem bItem) {
            //POI_MEMORIES.get(MemoryModuleType.JOB_SITE).
            Optional<PoiType> poiType = PoiType.forState(bItem.getBlock().defaultBlockState());
            return poiType.filter(ALL_JOBS).isPresent();
        }
        return false;
    }
}
