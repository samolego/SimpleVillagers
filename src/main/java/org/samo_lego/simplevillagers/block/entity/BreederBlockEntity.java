package org.samo_lego.simplevillagers.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.samo_lego.simplevillagers.gui.VillagerBlockGui;
import org.samo_lego.simplevillagers.gui.slot.FoodSlot;
import org.samo_lego.simplevillagers.gui.slot.OutputSlot;
import org.samo_lego.simplevillagers.gui.slot.VillagerSlot;
import org.samo_lego.simplevillagers.util.VillagerUtil;

import java.util.List;

import static net.minecraft.world.entity.npc.Villager.BREEDING_FOOD_THRESHOLD;
import static net.minecraft.world.entity.npc.Villager.FOOD_POINTS;
import static org.samo_lego.simplevillagers.SimpleVillagers.BREEDER_BLOCK_ENTITY;
import static org.samo_lego.simplevillagers.SimpleVillagers.CONFIG;
import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;
import static org.samo_lego.simplevillagers.SimpleVillagers.VILLAGER_ITEM;

public class BreederBlockEntity extends AbstractFarmBlockEntity {
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "breeder_block_entity");
    private int foodReserves;

    // Create baby villager item stack
    private static final ItemStack babyVillager = new ItemStack(VILLAGER_ITEM);


    public BreederBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BREEDER_BLOCK_ENTITY, blockPos, blockState);
    }

    @Override
    public void serverTick() {
        // Baby villagers

        this.growBabies();
        // Every 5 minutes => 20 ticks * 60 seconds * 5 minutes = 6000 ticks
        if (this.canOperate() && this.foodReserves >= BREEDING_FOOD_THRESHOLD && this.tickCount % CONFIG.breedingTimer == 0) {
            this.tickCount = 0;

            // If uncommented, imitates the villager's breeding behavior in vanilla
            // where villagers chew through food reserves even if there's no space (bed)
            // for babies.
            //this.decreaseFoodReserves();

            int size = this.getItems().size();
            for (int i = size - 4; i < size; i++) {
                if (this.items.get(i).isEmpty()) {
                    // Decrease food reserves
                    this.decreaseFoodReserves();

                    // Spawn particles
                    ((ServerLevel)this.level).sendParticles(ParticleTypes.HEART, this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5, 1, 0, 0, 0, 0.1);


                    // Create baby villager item stack
                    final ItemStack babyVillager = new ItemStack(VILLAGER_ITEM);
                    final CompoundTag babyTag = new CompoundTag();
                    babyTag.putInt("Age", CONFIG.babyAge);

                    // "Baby" lore
                    final CompoundTag loreTag = new CompoundTag();
                    final ListTag nbtLore = new ListTag();
                    nbtLore.add(StringTag.valueOf(Component.Serializer.toJson(new TextComponent("Baby"))));
                    loreTag.put(ItemStack.TAG_LORE, nbtLore);
                    babyTag.put(ItemStack.TAG_DISPLAY, loreTag);

                    babyVillager.setTag(babyTag);

                    this.items.set(i, babyVillager.copy());
                    break;
                }
            }
        }
    }

    @Override
    public int getScreenSize() {
        return 18;  // 2 rows * 9 slots
    }

    private void decreaseFoodReserves() {
        List<ItemStack> foods = this.items.stream().filter(stack -> FOOD_POINTS.containsKey(stack.getItem())).toList();

        int leftover = 0;
        for (ItemStack food : foods) {
            int ratio = 12 / FOOD_POINTS.get(food.getItem()) - leftover;

            if (food.getCount() >= ratio) {
                food.shrink(ratio);
                break;
            } else {
                leftover += food.getCount();
                food.setCount(0);
            }
        }
        this.foodReserves -= BREEDING_FOOD_THRESHOLD;
    }

    private void growBabies() {
        int size = this.getItems().size();
        for (int i = size - 4; i < size; i++) {
            final ItemStack stack = this.items.get(i);
            final CompoundTag tag = stack.getTag();

            if (tag != null && tag.contains("Age")) {
                int age = tag.getInt("Age");
                if (++age >= 0) {
                    tag.remove("Age");
                } else {
                    tag.putInt("Age", age);
                }
            }
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.foodReserves = this.countFood();
    }

    private int countFood() {
        return FOOD_POINTS.entrySet().stream().mapToInt(entry -> this.countItem(entry.getKey()) * entry.getValue()).sum();
    }

    @Override
    public void onUse(ServerPlayer player) {
        final ItemStack villagerStack = new ItemStack(VILLAGER_ITEM);
        villagerStack.setHoverName(new TranslatableComponent(EntityType.VILLAGER.getDescriptionId()).append(" ->"));
        villagerStack.enchant(null, 0);

        final ItemStack foodStack = new ItemStack(Items.CARROT);
        foodStack.setHoverName(new TranslatableComponent("itemGroup.food").append(" ->"));
        foodStack.enchant(null, 0);

        new VillagerBlockGui(MenuType.GENERIC_9x2, player, this,
                List.of(Pair.of(villagerStack, 2), Pair.of(foodStack, 4), Pair.of(villagerStack, 4)), this::getSlot).open();
    }

    private Slot getSlot(int index) {
        if (index < 2) {
            return new VillagerSlot(this, index);
        } else if (index < 6) {
            return new FoodSlot(this, index);
        }
        return new OutputSlot(this, index);
    }


    @Override
    protected void updateEmptyStatus(int index) {
        if (index < 2) {
            boolean canOperate = true;
            // Check first 2 slots for villagers
            for (int i = 0; i < 2; i++) {
                final ItemStack stack = this.items.get(i);
                canOperate &= stack.getItem() == VILLAGER_ITEM && !VillagerUtil.isBaby(stack);
            }

            // Update only if the status has changed
            if (canOperate != this.canOperate()) {
                this.setOperative(canOperate);
                super.updateEmptyStatus(index);
            }
        } else if (index < 6) {  // Check for food (index between 2 and 6)
            this.foodReserves = this.countFood();
        }
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.simplevillagers.breeder");
    }

    @Override
    public int getContainerSize() {
        return 10;
    }

    @Override
    public int[] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.DOWN) {
            // Last four slots are output, so those are returned
            return new int[]{6, 7, 8, 9};
        } else if (side == Direction.UP) {
            return new int[]{0, 1};
        }
        return new int[]{2, 3, 4, 5};
    }

    static {
        final CompoundTag babyTag = new CompoundTag();
        babyTag.putInt("Age", -24000);
        babyVillager.setTag(babyTag);
    }
}
