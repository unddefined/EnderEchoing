package com.unddefined.enderechoing.items;

import com.unddefined.enderechoing.client.model.WarpCoreModel;
import com.unddefined.enderechoing.client.renderer.item.WarpCoreRenderer;
import com.unddefined.enderechoing.network.packet.RenderEchoNamesPacket;
import com.unddefined.enderechoing.network.packet.SetEchoSoundingPosPacket;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.unddefined.enderechoing.Config.EECORE_TP_DISTANCE;
import static com.unddefined.enderechoing.server.registry.MobEffectRegistry.SCULK_VEIL;
import static net.minecraft.world.item.Rarity.EPIC;

public class WarpCore extends Item implements GeoItem {
    private int tick = 0;
    private int tick2 = 0;

    public WarpCore(Properties properties) {
        super(properties.stacksTo(1).rarity(EPIC));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity livingEntity) {
        return 40;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof ServerPlayer S)) return;
        Map<BlockPos, String> Map = new HashMap<>();
        if (S.hasEffect(SCULK_VEIL)) {
            PacketDistributor.sendToPlayer(S, new RenderEchoNamesPacket(Map));
            tick2 = 60;
            return;
        }
        if (tick2 > 0) {
            tick2--;
            return;
        }
        if (!isSelected) {
            if (tick > 0) {
                PacketDistributor.sendToPlayer(S, new SetEchoSoundingPosPacket(BlockPos.ZERO));
                PacketDistributor.sendToPlayer(S, new RenderEchoNamesPacket(Map));
            }
            tick = 0;
            return;
        }
        tick++;
        PacketDistributor.sendToPlayer(S, new SetEchoSoundingPosPacket(S.blockPosition()));
        if (tick < 24) return;
        int D = EECORE_TP_DISTANCE.get();
        var manager = MarkedPositionsManager.getManager(S);
        if (manager.teleporters().isEmpty() && manager.markedPositions().isEmpty()) return;
        manager.markedPositions().stream().filter(e -> e.dimension().equals(level.dimension()))
                .filter(e -> e.pos().distSqr(S.blockPosition()) < D * D * 4)
                .forEach(e -> Map.put(e.pos(), e.name()));
        PacketDistributor.sendToPlayer(S, new RenderEchoNamesPacket(Map));
    }


    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<WarpCore> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null) this.renderer = new WarpCoreRenderer(new WarpCoreModel());
                return this.renderer;
            }
        });
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return GeckoLibUtil.createInstanceCache(this);
    }
}
