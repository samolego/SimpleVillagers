package org.samo_lego.simplevillagers.mixin;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Villager.class)
public interface AVillager {
    @Invoker("startTrading")
    void callStartTrading(Player player);

    @Invoker("stopTrading")
    void callStopTrading();

    @Invoker("updateTrades")
    void callUpdateTrades();

    @Invoker("releaseAllPois")
    void callReleaseAllPois();

    @Invoker("increaseMerchantCareer")
    void callIncreaseMerchantCareer();

    @Invoker("updateSpecialPrices")
    void callUpdateSpecialPrices(Player player);

    @Accessor("increaseProfessionLevelOnUpdate")
    boolean increaseLevelOnTick();

    @Accessor("increaseProfessionLevelOnUpdate")
    void setIncreaseLevelOnTick(boolean enable);

    @Invoker("needsToRestock")
    boolean callNeedsToRestock();
}
