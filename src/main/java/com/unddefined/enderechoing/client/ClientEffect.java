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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class ClientEffect {
    // 存储从服务端同步过来的传送器位置
    private static List<BlockPos> syncedTeleporterPositions = new ArrayList<>();
    private static boolean TeleportReady = false;

    // 更新传送器位置的方法
    public static void updateTeleporterPositions(List<BlockPos> positions) {
        syncedTeleporterPositions = new ArrayList<>(positions);
    }

    @SubscribeEvent
    public static void renderEchoResponse(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
//        if (!TeleportReady) return;
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        // 使用从服务端同步过来的传送器位置
        for (BlockPos pos : syncedTeleporterPositions) {
            // 检查位置是否在渲染范围内
            if (new AABB(camera.getBlockPosition()).inflate(2048).contains(Vec3.atCenterOf(pos))) {
                Vec3 blockPos = Vec3.atCenterOf(pos);

                EchoResponse.render(
                        poseStack,
                        bufferSource,
                        blockPos.x - camera.getPosition().x,
                        blockPos.y - camera.getPosition().y + 1,
                        blockPos.z - camera.getPosition().z,
                        blockPos,
                        event.getPartialTick().getGameTimeDeltaTicks(),
                        level.getGameTime(),
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
}