package com.unddefined.enderechoing.items;

import com.unddefined.enderechoing.Config;
import com.unddefined.enderechoing.client.ClientEvent;
import com.unddefined.enderechoing.client.model.EnderEchoingCoreModel;
import com.unddefined.enderechoing.client.renderer.item.EnderEchoingCoreRenderer;
import com.unddefined.enderechoing.network.packet.SyncTeleportersPacket;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import com.unddefined.enderechoing.server.registry.MobEffectRegistry;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

import static com.unddefined.enderechoing.server.registry.MobEffectRegistry.SCULK_VEIL;
import static net.minecraft.core.component.DataComponents.CUSTOM_NAME;

public class EnderEchoingCore extends Item implements GeoItem {
    private static final String CONTROLLER_NAME = "controller";
    private static final String ANIM_USE = "use";
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
                if (this.renderer == null) {
                    this.renderer = new EnderEchoingCoreRenderer(new EnderEchoingCoreModel());
                }

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, CONTROLLER_NAME, 0, state -> PlayState.STOP)
                .triggerableAnim(ANIM_USE, USE_ANIM));
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity livingEntity) {
        return 40;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        ClientEvent.TeleportReady = true;

        // 渲染传送特效
        MarkedPositionsManager manager = MarkedPositionsManager.getTeleporters(level);
        if (manager != null && manager.hasTeleporters()) {
            // 创建同步数据包
            SyncTeleportersPacket packet = new SyncTeleportersPacket(manager.getNearestTeleporter(level,player.blockPosition()));
            // 向在线玩家发送数据包
            if (player instanceof ServerPlayer splayer) {
                splayer.addEffect(new MobEffectInstance(SCULK_VEIL, 300));
                PacketDistributor.sendToPlayer(splayer, packet);
            }
        }
        // 检查是否在冷却中
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(itemStack);
        }
        // 检查玩家是否发光，如果发光则无法使用
        if (player.isCurrentlyGlowing()) {
            return InteractionResultHolder.fail(itemStack);
        }
        player.startUsingItem(hand);
        // 添加动画
        if (level instanceof ServerLevel serverLevel) {
            player.addEffect(new MobEffectInstance(MobEffectRegistry.SCULK_VEIL, 20 * 3, 0, false, true));
            triggerAnim(player, GeoItem.getOrAssignId(itemStack, serverLevel), CONTROLLER_NAME, ANIM_USE);
        }
        if (level.isClientSide()) {
            if (player instanceof AbstractClientPlayer clientPlayer) {
                AnimationStack animationStack = PlayerAnimationAccess.getPlayerAnimLayer(clientPlayer);
                // 移除之前的动画层（如果存在）
                animationStack.removeLayer(42);
                
                // 添加新的动画层
                ModifierLayer<IAnimation> playerAnimation = new ModifierLayer<>();
                playerAnimation.setAnimation(PlayerAnimationRegistry
                        .getAnimation(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echoing_core.player.use"))
                        .playAnimation());
                animationStack.addAnimLayer(42, playerAnimation);
            }
        }

        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        // 当玩家释放使用物品时，移除动画层
        super.releaseUsing(stack, level, livingEntity, timeLeft);
        ClientEvent.TeleportReady = false;
        if (level.isClientSide() && livingEntity instanceof AbstractClientPlayer clientPlayer) {
            AnimationStack animationStack = PlayerAnimationAccess.getPlayerAnimLayer(clientPlayer);
            animationStack.removeLayer(42);
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && livingEntity instanceof Player player) {
            stopTriggeredAnim(player, GeoItem.getOrAssignId(stack, serverLevel), CONTROLLER_NAME, null);
            player.addEffect(new MobEffectInstance(MobEffects.GLOWING,400));

        }
    }
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && livingEntity instanceof Player player) {
            // 再次检查玩家是否有未保存数据的末影珍珠
            if (!player.getInventory().hasAnyMatching(itemStack ->
                    itemStack.getItem() == ItemRegistry.ENDER_ECHOING_PEARL.get() && itemStack.get(CUSTOM_NAME) == null)) {
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING,400));
                ClientEvent.TeleportReady = false;
                return stack;
            }

            // 查找最近的EnderEchoicResonator方块
            MarkedPositionsManager manager = MarkedPositionsManager.getTeleporters(level);
            var nearestTeleporterPos = manager.getNearestTeleporter(serverLevel, player.blockPosition());

            if (nearestTeleporterPos == null) {
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING,400));
                ClientEvent.TeleportReady = false;
                return stack;
            }

            // 消耗一个没有保存数据的珍珠
            player.getInventory().clearOrCountMatchingItems(itemStack -> 
                            itemStack.getItem() == ItemRegistry.ENDER_ECHOING_PEARL.get() && 
                            itemStack.get(CUSTOM_NAME) == null,
                    1, player.inventoryMenu.getCraftSlots());

            // 传送玩家到最近的传送器位置
            if (player instanceof ServerPlayer serverPlayer) {
                var pos = nearestTeleporterPos.getFirst();
                serverPlayer.teleportTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                level.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                ClientEvent.TeleportReady = false;
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING,300));
            }
            // 设置冷却时间
            player.getCooldowns().addCooldown(this, Config.ENDER_ECHOING_CORE_COOLDOWN.get());
        }
        if (level.isClientSide() && livingEntity instanceof Player player && !player.isUsingItem()) {
            if (player instanceof AbstractClientPlayer clientPlayer) {
                AnimationStack playerAnim = PlayerAnimationAccess.getPlayerAnimLayer(clientPlayer);
                playerAnim.removeLayer(42);
            }
        }
        return stack;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


}