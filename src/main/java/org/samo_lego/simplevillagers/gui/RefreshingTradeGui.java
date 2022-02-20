package org.samo_lego.simplevillagers.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.MerchantGui;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.samo_lego.simplevillagers.mixin.AVillager;
import org.samo_lego.simplevillagers.util.VillagerUtil;

public class RefreshingTradeGui extends MerchantGui {

    private static final ItemStack BARRIER = new ItemStack(Items.BARRIER);
    private static final ItemStack TERACOTTA = new ItemStack(Items.MAGENTA_GLAZED_TERRACOTTA);
    private final Villager villager;

    /**
     * Constructs a new MerchantGui for the supplied player.
     *
     * @param player the player to serve this gui to
     * @param villager the villager to trade with
     */
    public RefreshingTradeGui(ServerPlayer player, Villager villager) {
        super(player, false);
        this.villager = villager;
        this.merchant.overrideOffers(this.villager.getOffers());
        this.setExperience(0);
        this.setTitle(this.villager.getDisplayName());

        GuiElementInterface refreshBtn = new GuiElement(TERACOTTA, this::rerollTrades);
        this.setSlot(0, refreshBtn);

        GuiElementInterface hideButtonsBtn = new GuiElement(BARRIER, this::hideButtons);
        this.setSlot(1, hideButtonsBtn);
    }

    private void hideButtons(int i, ClickType clickType, net.minecraft.world.inventory.ClickType clickType1) {
        this.openDefaultTradeMenu();
    }

    private void rerollTrades(int i, ClickType clickType, net.minecraft.world.inventory.ClickType clickType1) {
        this.villager.getOffers().clear();
        ((AVillager) this.villager).callUpdateTrades();

        this.merchant.overrideOffers(this.villager.getOffers());
        this.sendUpdate();
    }

    @Override
    public void onSelectTrade(MerchantOffer offer) {
        this.merchant.overrideOffers(new MerchantOffers());
        this.openDefaultTradeMenu();
        //this.villager.(offer);

        AbstractContainerMenu abstractContainerMenu = this.player.containerMenu;
        if (abstractContainerMenu instanceof MerchantMenu merchantMenu) {
            int i = this.villager.getOffers().indexOf(offer);
            merchantMenu.setSelectionHint(i);
            merchantMenu.tryMoveItems(i);
        }
    }

    public void openDefaultTradeMenu() {
        this.close();
        ((VillagerUtil) this.villager).forceDefaultTradingScreen();
        ((AVillager) this.villager).callStartTrading(this.player);
    }

    @Override
    public boolean onTrade(MerchantOffer offer) {
        return true;
    }

    static {
        BARRIER.setHoverName(new TranslatableComponent("simplevillagers.hide_buttons"));
        TERACOTTA.setHoverName(new TranslatableComponent("simplevillagers.reroll_trades"));
    }
}
