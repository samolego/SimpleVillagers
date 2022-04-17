package org.samo_lego.simplevillagers.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.simplevillagers.gui.VillagerBlockGui;
import org.samo_lego.simplevillagers.gui.slot.VillagerSlot;
import org.samo_lego.simplevillagers.gui.slot.WorkstationSlot;
import org.samo_lego.simplevillagers.mixin.AVillager;
import org.samo_lego.simplevillagers.util.VillagerUtil;

import java.util.List;
import java.util.Optional;

import static org.samo_lego.simplevillagers.SimpleVillagers.CONFIG;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;
import static org.samo_lego.simplevillagers.SimpleVillagers.TRADING_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.VILLAGER_ITEM;
import static org.samo_lego.simplevillagers.item.VillagerItem.loadVillager;

public class TradingBlockEntity extends AbstractFarmBlockEntity {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "trading_block_entity");
    private VillagerProfession activeProfession;
    private Villager villager;
    private int restocks;
    private long restockDay;
    private long restockTime;

    public TradingBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TRADING_BLOCK_ENTITY, blockPos, blockState);
        this.activeProfession = VillagerProfession.NONE;
    }

    @Override
    public void serverTick() {
        if (this.canOperate() && this.villager != null) {
            if (((AVillager) this.villager).increaseLevelOnTick()) {
                // Increase villager level
                this.villager.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));

                if (this.villager.isTrading() && this.villager.getTradingPlayer() instanceof ServerPlayer pl) {
                    pl.sendMerchantOffers(pl.containerMenu.containerId,
                            this.villager.getOffers(),
                            this.villager.getVillagerData().getLevel(),
                            this.villager.getVillagerXp(),
                            this.villager.showProgressBar(),
                            this.villager.canRestock());
                }

                ((AVillager) this.villager).callIncreaseMerchantCareer();
                ((AVillager) this.villager).setIncreaseLevelOnTick(false);
            }

            // Restocking
            final long gameTime = this.level.getGameTime();
            if (((AVillager) this.villager).callNeedsToRestock() && (gameTime - this.restockTime) % 24000L > CONFIG.restock.minWaitTime && (this.restocks < CONFIG.restock.maxAmount || gameTime / 24000L > this.restockDay)) {
                this.villager.restock();
                ++this.restocks;
                this.restockDay = gameTime / 24000L;
                this.restockTime = gameTime % 24000L;
            }
        } else if (this.villager == null && this.canOperate()) {
            this.prepareVillager();
        }
    }


    @Override
    public void onUse(ServerPlayer player) {
        if (this.canOperate() && this.villager != null && !player.isShiftKeyDown()) {

            ((AVillager) this.villager).callUpdateSpecialPrices(player);
            this.villager.setTradingPlayer(player);
            this.villager.openTradingScreen(player, this.villager.getDisplayName(), this.villager.getVillagerData().getLevel());

            player.awardStat(Stats.TALKED_TO_VILLAGER);
        } else {
            final ItemStack left = new ItemStack(VILLAGER_ITEM);
            left.setHoverName(new TranslatableComponent(EntityType.VILLAGER.getDescriptionId()).append(" ->"));
            left.enchant(null, 0);

            final ItemStack right = new ItemStack(Items.LECTERN);
            right.setHoverName(new TranslatableComponent("todo").append(" ->"));
            right.enchant(null, 0);

            new VillagerBlockGui(MenuType.GENERIC_9x1, player, this, List.of(Pair.of(left, 1), Pair.of(right, 1)), this::getSlot).open();
        }
    }

    private Slot getSlot(int index) {
        return index == 0 ? new VillagerSlot(this, index) : new WorkstationSlot(this, index);
    }

    @Override
    protected void updateEmptyStatus(int index) {
        final ItemStack stack = this.items.get(0);
        boolean canOperate = stack.getItem() == VILLAGER_ITEM && VillagerUtil.isParent(stack);

        // Change profession
        final ItemStack profBlock = this.items.get(1);
        if (!profBlock.isEmpty() && canOperate) {
            Optional<PoiType> poiType = PoiType.forState(((BlockItem) profBlock.getItem()).getBlock().defaultBlockState());
            // set villager profession from poi type
            poiType.ifPresent(type -> this.activeProfession = Registry.VILLAGER_PROFESSION.get(ResourceLocation.tryParse(type.getName())));
            canOperate = this.activeProfession != VillagerProfession.NONE;
        } else {
            canOperate = false;
            this.villager = null;
        }

        // Update only if the status has changed
        if (canOperate != this.canOperate()) {
            this.setOperative(canOperate);
            if (canOperate) {
                // Try to set villager profession
                this.prepareVillager();
            }
            super.updateEmptyStatus(index);
        }
    }

    private void prepareVillager() {
        this.villager = EntityType.VILLAGER.create(this.level);
        loadVillager(this.villager, this.items.get(0));
        final var data = this.villager.getVillagerData();
        final var profession = data.getProfession();

        if (profession == VillagerProfession.NONE) {
            this.villager.setVillagerData(data.setProfession(this.activeProfession));

        } else if (profession != this.activeProfession) {
            this.setOperative(false);
        }
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        if (index == 0 && count > 0 && this.villager != null) {
            if (this.villager.isTrading())
                this.villager.setTradingPlayer(null);

            ItemStack stack = this.items.get(0);

            if (stack.getItem() == VILLAGER_ITEM) {
                VillagerUtil.saveVillager(this.villager, stack, false);
                System.out.println(villager.getVillagerXp());
                this.setOperative(false);
                this.villager = null;
            }
        }
        return super.removeItem(index, count);
    }


    @Override
    public void setItem(int index, ItemStack stack) {
        if (index == 0 && this.villager != null && stack.isEmpty()) {
            if (this.villager.isTrading())
                this.villager.setTradingPlayer(null);

            final var itemStack = this.items.get(0);

            if (itemStack.getItem() == VILLAGER_ITEM) {
                VillagerUtil.saveVillager(this.villager, itemStack, false);
                System.out.println(villager.getVillagerXp());
                this.villager = null;
            }
        }
        super.setItem(index, stack);
    }

    @Override
    public int getScreenSize() {
        return 9;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.simplevillagers.trader");
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public void onDestroy(ServerPlayer player) {
        if (this.villager != null && !this.level.isClientSide()) {
            if (this.villager.isTrading()) {
                ((AVillager) this.villager).callStopTrading();
                ((ServerPlayer) this.villager.getTradingPlayer()).closeContainer();
            }
            VillagerUtil.saveVillager(this.villager, this.items.get(0), false);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        if (this.villager != null) {
            VillagerUtil.saveVillager(this.villager, this.items.get(0), false);
        }
        super.saveAdditional(tag);
    }
}
