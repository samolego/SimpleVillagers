package org.samo_lego.simplevillagers.mixin;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.samo_lego.simplevillagers.gui.RefreshingTradeGui;
import org.samo_lego.simplevillagers.util.VillagerUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager implements VillagerUtil {

    @Unique
    private boolean force = false;

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void forceDefaultTradingScreen(boolean force) {
        this.force = force;
    }

    @Inject(method = "startTrading",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/npc/Villager;openTradingScreen(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/network/chat/Component;I)V"),
            cancellable = true)
    private void onStartTrading(Player player, CallbackInfo ci) {
        if (player instanceof ServerPlayer pl &&
                this.getVillagerXp() == 0 &&
                Permissions.check(pl, "simplevillagers.reroll_buttons", true) &&
                !this.force) {
            new RefreshingTradeGui(pl, (Villager) (Object) this).open();
            ci.cancel();
        } else {
            this.force = false;
        }
    }
}
