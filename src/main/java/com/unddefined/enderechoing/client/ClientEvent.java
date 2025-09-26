package com.unddefined.enderechoing.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.renderer.EchoResponse;
import com.unddefined.enderechoing.client.renderer.layer.SculkVeilLayer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class ClientEvent {
    public static boolean TeleportReady = false;
    public static BlockPos TeleportFrom = null;
    // 存储从服务端同步过来的传送器位置
    private static List<BlockPos> syncedTeleporterPositions = new ArrayList<>();

    // 更新传送器位置的方法
    public static void updateTeleporterPositions(List<BlockPos> positions) {
        syncedTeleporterPositions = new ArrayList<>(positions);
    }

    @SubscribeEvent
    public static void renderEchoResponse(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (!TeleportReady) return;
        PoseStack poseStack = new PoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        // 使用从服务端同步过来的传送器位置
        for (BlockPos pos : syncedTeleporterPositions) {
            if (pos.equals(TeleportFrom)) continue;

            // 检查位置是否在渲染范围内
            if (new AABB(camera.getBlockPosition()).inflate(8192).contains(Vec3.atCenterOf(pos))) {
                Vec3 blockPos = Vec3.atCenterOf(pos);
                EchoResponse.render(
                        poseStack, bufferSource,
                        blockPos.x, blockPos.y + 1, blockPos.z, blockPos,
                        event.getPartialTick().getGameTimeDeltaTicks(), level.getGameTime(),
                        0xF000F0 // full brightness
                );
            }
        }
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        // 为玩家渲染器添加影匿渲染层（有bug）
        event.getSkins().forEach((skin) -> {
            EntityRenderer<? extends Player> playerRenderer = event.getSkin(skin);
            if (playerRenderer instanceof PlayerRenderer renderer) {
                renderer.addLayer(new SculkVeilLayer(renderer));
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        // 玩家登出时重置传送数据
        TeleportReady = false;
        TeleportFrom = null;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!TeleportReady || TeleportFrom == null) return;
        else if (event.getEntity().level().isClientSide) {
            if (!event.getEntity().blockPosition().equals(TeleportFrom)) {
                // 玩家离开了方块，重置状态
                TeleportReady = false;
                TeleportFrom = null;
                event.getEntity().addEffect(new MobEffectInstance(MobEffects.GLOWING,400));
            }
        }


    }
}