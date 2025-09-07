package com.unddefined.enderechoing.items;

import com.unddefined.enderechoing.Config;
import com.unddefined.enderechoing.util.TeleporterManager;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class EnderEchoingCore extends Item implements GeoItem {
    private static final RawAnimation USE_ANIM = RawAnimation.begin().thenPlay("ender_echoing_core.use");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public EnderEchoingCore(Properties properties) {
        super(properties.stacksTo(1));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<EnderEchoingCore> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeoItemRenderer<>(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echoing_core")));

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Activation", 0, state -> PlayState.STOP)
                .triggerableAnim("ender_echoing_core.use", USE_ANIM));
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 40;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW; // 显示使用动画
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel) {
            triggerAnim(player, GeoItem.getOrAssignId(player.getItemInHand(hand), serverLevel), "Activation", "ender_echoing_core.use");
        }
        // 检查是否在冷却中
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(itemStack);
        }

        // 检查玩家是否有末影珍珠
        if (!player.getInventory().hasAnyMatching(stack -> stack.getItem() == Items.ENDER_PEARL)) {
            if (level.isClientSide()) {
            }
            return InteractionResultHolder.fail(itemStack);
        }

        // 检查是否有传送器
        if (level instanceof ServerLevel serverLevel) {
            TeleporterManager manager = TeleporterManager.get(level);
            BlockPos nearestTeleporterPos = manager.getNearestTeleporter(serverLevel, player.blockPosition());
            if (nearestTeleporterPos == null) {
                return InteractionResultHolder.fail(itemStack);
            }
        }

        player.startUsingItem(hand);
        return super.use(level, player, hand);
    }

    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && livingEntity instanceof Player player) {
            // 再次检查玩家是否有末影珍珠
            if (!player.getInventory().hasAnyMatching(itemStack -> itemStack.getItem() == Items.ENDER_PEARL)) {
                return stack;
            }

            // 查找最近的EnderEchoicTeleporter方块
            TeleporterManager manager = TeleporterManager.get(level);
            BlockPos nearestTeleporterPos = manager.getNearestTeleporter(serverLevel, player.blockPosition());

            if (nearestTeleporterPos == null) {
                return stack;
            }

            // 消耗一个末影珍珠
            player.getInventory().clearOrCountMatchingItems(itemStack -> itemStack.getItem() == Items.ENDER_PEARL, 1, player.inventoryMenu.getCraftSlots());

            // 传送玩家到最近的传送器位置
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.teleportTo(nearestTeleporterPos.getX() + 0.5, nearestTeleporterPos.getY() + 1, nearestTeleporterPos.getZ() + 0.5);

                // 播放传送声音
                level.playSound(null, nearestTeleporterPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            // 设置冷却时间
            player.getCooldowns().addCooldown(this, Config.ENDER_ECHOING_CORE_COOLDOWN.get());
        }

        return stack;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}