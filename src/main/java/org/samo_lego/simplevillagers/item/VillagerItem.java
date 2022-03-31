package org.samo_lego.simplevillagers.item;

import eu.pb4.polymer.api.item.SimplePolymerItem;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static org.samo_lego.simplevillagers.SimpleVillagers.MOD_ID;

public class VillagerItem extends SimplePolymerItem {

    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "villager_item");

    public VillagerItem(Properties settings) {
        super(settings, Items.VILLAGER_SPAWN_EGG);
    }

    @Override
    public Component getName(@NotNull ItemStack stack) {
        final CompoundTag tag = stack.getTag();

        if (tag != null) {
            if (tag.contains("CustomName")) {
                final String customName = tag.getString("CustomName");
                return Component.Serializer.fromJson(customName);
            } else if (tag.contains("VillagerData")) {
                final ResourceLocation id = new ResourceLocation(tag.getCompound("VillagerData").getString("profession"));
                return new TranslatableComponent(EntityType.VILLAGER.getDescriptionId() + "." + id.getPath());
            }
        }

        return super.getName(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final Level level = context.getLevel();

        if (!(level instanceof ServerLevel world)) {
            return InteractionResult.SUCCESS;
        }

        final Player player = context.getPlayer();
        if (player != null && !Permissions.check(player, "simplevillagers.villager_item.use", true)) {
            return InteractionResult.FAIL;
        }

        final ItemStack handStack = context.getItemInHand();
        final BlockPos clickedPos = context.getClickedPos();
        final Direction direction = context.getClickedFace();
        final BlockState blockState = level.getBlockState(clickedPos);

        final BlockPos pos = blockState.getCollisionShape(level, clickedPos).isEmpty() ? clickedPos : clickedPos.relative(direction);

        final Entity villager = EntityType.VILLAGER.create(world);

        if (villager != null) {
            loadVillager(villager, handStack);
            villager.setPos(Vec3.atCenterOf(pos));
            world.addFreshEntity(villager);

            handStack.shrink(1);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, clickedPos);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        final ItemStack handStack = player.getItemInHand(usedHand);
        final BlockHitResult hitResult = SpawnEggItem.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (!(level instanceof ServerLevel)) {
            return InteractionResultHolder.success(handStack);
        }

        if (!Permissions.check(player, "simplevillagers.villager_item.spawn", true)) {
            return InteractionResultHolder.fail(handStack);
        }

        final BlockPos blockPos = hitResult.getBlockPos();
        if (((HitResult) hitResult).getType() != HitResult.Type.BLOCK ||
            !(level.getBlockState(blockPos).getBlock() instanceof LiquidBlock)) {
            return InteractionResultHolder.pass(handStack);
        }

        if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(blockPos, hitResult.getDirection(), handStack)) {
            return InteractionResultHolder.fail(handStack);
        }

        final Entity villager = EntityType.VILLAGER.create(level);
        if (villager == null) {
            return InteractionResultHolder.pass(handStack);
        }

        loadVillager(villager, handStack);
        villager.setPos(Vec3.atCenterOf(blockPos));

        level.addFreshEntity(villager);

        if (!player.getAbilities().instabuild) {
            handStack.shrink(1);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        level.gameEvent(GameEvent.ENTITY_PLACE, player);

        return InteractionResultHolder.consume(handStack);
    }

    public static void loadVillager(Entity villager, ItemStack handStack) {
        final CompoundTag tag = handStack.getOrCreateTag();
        tag.put("Pos", newDoubleList(villager.getX(), villager.getY(), villager.getZ()));

        villager.load(tag);
    }

    private static ListTag newDoubleList(double ... numbers) {
        ListTag listTag = new ListTag();
        for (double d : numbers) {
            listTag.add(DoubleTag.valueOf(d));
        }
        return listTag;
    }
}
