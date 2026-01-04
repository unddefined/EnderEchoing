package com.unddefined.enderechoing.items;

import com.unddefined.enderechoing.Config;
import com.unddefined.enderechoing.client.model.EnderEchoingCoreModel;
import com.unddefined.enderechoing.client.renderer.item.EnderEchoingCoreRenderer;
import com.unddefined.enderechoing.network.packet.OpenEditScreenPacket;
import com.unddefined.enderechoing.network.packet.SetEchoSoundingPosPacket;
import com.unddefined.enderechoing.network.packet.SetPlayerAnimationPacket;
import com.unddefined.enderechoing.network.packet.SetTeleportPosPacket;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import com.unddefined.enderechoing.server.registry.MobEffectRegistry;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

import static com.unddefined.enderechoing.server.registry.DataRegistry.EE_PEARL_AMOUNT;
import static com.unddefined.enderechoing.server.registry.DataRegistry.EE_PEARL_POSITION;
import static net.minecraft.core.component.DataComponents.CUSTOM_NAME;

public class EnderEchoingCore extends Item implements GeoItem {
    private static final String CONTROLLER_NAME = "controller";
    private static final String ANIM_USE = "use";
    private static final RawAnimation USE_ANIM = RawAnimation.begin().thenPlay("ender_echoing_core.use");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public EnderEchoingCore(Properties properties) {
        super(properties.stacksTo(2));
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
    public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity livingEntity) {return 40;}

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {return UseAnim.CUSTOM;}

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown() && player instanceof ServerPlayer S) {
            // 检查是否在冷却中
            if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(itemStack);
            // 检查玩家是否发光，如果发光则无法使用
            if (player.isCurrentlyGlowing()) return InteractionResultHolder.fail(itemStack);
            // 查找最近的EnderEchoicResonator方块
            var manager = MarkedPositionsManager.getManager(player);
            var nearestTeleporterPos = manager.getNearestTeleporter(level, player.blockPosition());
            if (nearestTeleporterPos.getFirst() == null) return InteractionResultHolder.fail(itemStack);
            // 检查玩家是否有未保存数据的末影回响珍珠
            if (!player.getInventory().hasAnyMatching(item ->
                    item.getItem() == ItemRegistry.ENDER_ECHOING_PEARL.get() && item.get(CUSTOM_NAME) == null)
                    || player.getData(EE_PEARL_AMOUNT.get()) < 1)
                return InteractionResultHolder.fail(itemStack);
            // 渲染传送特效
            PacketDistributor.sendToPlayer(S, new SetEchoSoundingPosPacket(player.blockPosition()));
            PacketDistributor.sendToPlayer(S, new SetTeleportPosPacket(nearestTeleporterPos.getFirst(), true));
            // 添加玩家动画
            PacketDistributor.sendToPlayer(S, new SetPlayerAnimationPacket());
            // 添加动画
            player.addEffect(new MobEffectInstance(MobEffectRegistry.SCULK_VEIL, 20 * 3, 0, false, true));
            if (level instanceof ServerLevel SL) triggerAnim(player, GeoItem.getOrAssignId(itemStack, SL), CONTROLLER_NAME, ANIM_USE);

            player.startUsingItem(hand);
        } else if (player.getData(EE_PEARL_AMOUNT.get()) > 0 || player.getInventory().hasAnyMatching(stack ->
                stack.getItem() == ItemRegistry.ENDER_ECHOING_PEARL.get() && stack.get(CUSTOM_NAME) == null)) {
            if (!level.isClientSide()) PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenEditScreenPacket("", player.blockPosition()));
            player.setData(EE_PEARL_POSITION.get(), player.blockPosition());
        }

        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        // 当玩家释放使用物品时，移除动画层
        super.releaseUsing(stack, level, livingEntity, timeLeft);
        if (livingEntity instanceof AbstractClientPlayer clientPlayer) {
            AnimationStack animationStack = PlayerAnimationAccess.getPlayerAnimLayer(clientPlayer);
            animationStack.removeLayer(42);
        }

        if (level instanceof ServerLevel SL && livingEntity instanceof ServerPlayer S) {
            stopTriggeredAnim(S, GeoItem.getOrAssignId(stack, SL), CONTROLLER_NAME, null);
            S.addEffect(new MobEffectInstance(MobEffects.GLOWING, 400));
            PacketDistributor.sendToPlayer(S, new SetEchoSoundingPosPacket(BlockPos.ZERO));
            PacketDistributor.sendToPlayer(S, new SetTeleportPosPacket(BlockPos.ZERO, false));
        }
    }

    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        if (level instanceof ServerLevel && livingEntity instanceof ServerPlayer player) {
            // 再次检查玩家是否有未保存数据的珍珠
            if (!player.getInventory().hasAnyMatching(itemStack ->
                    itemStack.getItem() == ItemRegistry.ENDER_ECHOING_PEARL.get() && itemStack.get(CUSTOM_NAME) == null)
                    || player.getData(EE_PEARL_AMOUNT.get()) < 1) {
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300));
                PacketDistributor.sendToPlayer(player, new SetEchoSoundingPosPacket(BlockPos.ZERO));
                return stack;
            }
            // 消耗一个没有保存数据的珍珠
            if (player.getData(EE_PEARL_AMOUNT.get()) > 0) player.setData(EE_PEARL_AMOUNT.get(), player.getData(EE_PEARL_AMOUNT.get()) - 1);
            else player.getInventory().clearOrCountMatchingItems(itemStack ->
                    itemStack.getItem() == ItemRegistry.ENDER_ECHOING_PEARL.get() &&
                            itemStack.get(CUSTOM_NAME) == null, 1, player.inventoryMenu.getCraftSlots());

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
    public AnimatableInstanceCache getAnimatableInstanceCache() {return cache;}
}