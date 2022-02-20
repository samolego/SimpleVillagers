package org.samo_lego.simplevillagers.mixin;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Villager.class)
public interface AVillager {
    @Invoker("startTrading")
    void callStartTrading(Player player);

    @Invoker("updateTrades")
    void callUpdateTrades();
}
