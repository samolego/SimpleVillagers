package org.samo_lego.simplevillagers.gui;

import eu.pb4.sgui.api.ClickType;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementInterface;
import eu.pb4.sgui.api.gui.MerchantGui;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.samo_lego.simplevillagers.mixin.AVillager;
import org.samo_lego.simplevillagers.util.VillagerUtil;

public class RefreshingTradeGui extends MerchantGui {

    private static final ItemStack BARRIER = new ItemStack(Items.BARRIER);
    private static final ItemStack TERACOTTA = new ItemStack(Items.MAGENTA_GLAZED_TERRACOTTA);
    private static final StringTag LINE = StringTag.valueOf(Component.Serializer.toJson(new TextComponent("---------------")
            .withStyle(ChatFormatting.GRAY)
            .withStyle(Style.EMPTY.withItalic(false))));
    private final Villager villager;
    private final ItemStack rerollStack;

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

        this.rerollStack = TERACOTTA.copy();
        this.updateLore();
        GuiElementInterface refreshBtn = new GuiElement(rerollStack, this::rerollTrades);
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

        //this.merchant.overrideOffers(newTrades);
        this.updateLore();

        this.sendUpdate();
    }

    private void updateLore() {
        final MerchantOffers newTrades = this.villager.getOffers();
        // Update lore
        final CompoundTag nbtDisplay = this.rerollStack.getOrCreateTag().getCompound(ItemStack.TAG_DISPLAY);
        final ListTag nbtLore = new ListTag();

        for (MerchantOffer offer : newTrades) {
            // A stack of the offer
            int aCount = offer.getCostA().getCount();
            final Component aName = offer.getCostA().getHoverName();

            final MutableComponent loreTextA = new TextComponent(aCount + "x ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(aName)
                    .withStyle(Style.EMPTY.withItalic(false));
            nbtLore.add(StringTag.valueOf(Component.Serializer.toJson(loreTextA)));

            // B stack
            final MutableComponent loreText = new TextComponent("");
            final ItemStack costB = offer.getCostB();
            if (!costB.isEmpty()) {
                int bCount = costB.getCount();
                final Component bName = costB.getHoverName();

                loreText.append(new TextComponent(bCount + "x ").append(bName).withStyle(ChatFormatting.DARK_GREEN).withStyle(Style.EMPTY.withItalic(false)));
            }

            // Result
            final ItemStack result = offer.getResult();
            int resCount = result.getCount();
            final Component resName = result.getHoverName();
            loreText.append(new TextComponent(" -> ").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(Style.EMPTY.withItalic(false)))
                    .append(new TextComponent( resCount + "x ")
                            .append(resName)
                            .withStyle(ChatFormatting.GOLD)
                            .withStyle(ChatFormatting.BOLD))
                    .withStyle(Style.EMPTY.withItalic(false));
            nbtLore.add(StringTag.valueOf(Component.Serializer.toJson(loreText)));

            // Easier enchant finding
            final CompoundTag enchantmentTag = result.getTag();

            if (enchantmentTag != null && enchantmentTag.contains("StoredEnchantments", 9)) {
                final MutableComponent enchants = new TextComponent("  + Enchants:")
                        .withStyle(Style.EMPTY.withItalic(false))
                        .withStyle(ChatFormatting.GRAY);
                nbtLore.add(StringTag.valueOf(Component.Serializer.toJson(enchants)));

                for (Tag tag : enchantmentTag.getList("StoredEnchantments", 10)) {
                    final CompoundTag enchantTag = (CompoundTag) tag;
                    final MutableComponent txt = new TextComponent("    - ").withStyle(ChatFormatting.DARK_PURPLE);

                    final ResourceLocation id = new ResourceLocation(enchantTag.getString("id"));
                    short lvl = enchantTag.getShort("lvl");

                    final Enchantment enchantment = Registry.ENCHANTMENT.get(id);
                    boolean max = enchantment.getMaxLevel() == lvl;
                    final ChatFormatting color = enchantment.isCurse() ?
                            ChatFormatting.RED :
                            max ?
                                    ChatFormatting.AQUA :
                                    ChatFormatting.DARK_PURPLE;

                    txt.append(new TranslatableComponent(enchantment.getDescriptionId()).withStyle(color));

                    if (lvl > 1) {
                        txt.append(new TextComponent(" " + lvl).withStyle(max ? ChatFormatting.BLUE : ChatFormatting.LIGHT_PURPLE));
                    }
                    nbtLore.add(StringTag.valueOf(Component.Serializer.toJson(txt)));
                }
            }

            nbtLore.add(LINE);
        }

        nbtDisplay.put(ItemStack.TAG_LORE, nbtLore);
        this.rerollStack.getTag().put(ItemStack.TAG_DISPLAY, nbtDisplay);
    }

    @Override
    public void onSelectTrade(MerchantOffer offer) {
        this.merchant.overrideOffers(new MerchantOffers());  // Clear offers so that method that calls this doesn't override our items
        this.openDefaultTradeMenu();

        AbstractContainerMenu abstractContainerMenu = this.player.containerMenu;
        if (abstractContainerMenu instanceof MerchantMenu merchantMenu) {
            int i = this.villager.getOffers().indexOf(offer);
            merchantMenu.setSelectionHint(i);
            merchantMenu.tryMoveItems(i);
        }
    }

    public void openDefaultTradeMenu() {
        this.close();
        ((VillagerUtil) this.villager).forceDefaultTradingScreen(true);
        ((AVillager) this.villager).callStartTrading(this.player);
    }

    @Override
    public boolean onTrade(MerchantOffer offer) {
        return false;
    }

    @Override
    public void onClose() {
        this.villager.setTradingPlayer(null);
        ((VillagerUtil) this.villager).forceDefaultTradingScreen(false);
    }

    static {
        BARRIER.setHoverName(new TranslatableComponent("simplevillagers.hide_buttons"));
        TERACOTTA.setHoverName(new TranslatableComponent("simplevillagers.reroll_trades"));
    }
}
