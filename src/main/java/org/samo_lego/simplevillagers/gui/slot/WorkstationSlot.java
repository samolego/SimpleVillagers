package org.samo_lego.simplevillagers.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;


public class WorkstationSlot extends Slot {
    public WorkstationSlot(Container container, int i) {
        super(container, i, 0, 0);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem bItem) {
            // todo test in 1.19
            var poiType = PoiTypes.forState(bItem.getBlock().defaultBlockState());
            return poiType.isPresent();
        }
        return false;
    }
}
