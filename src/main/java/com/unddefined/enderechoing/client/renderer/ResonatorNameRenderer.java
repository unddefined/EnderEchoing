package com.unddefined.enderechoing.client.renderer;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.client.renderer.LightTexture.FULL_BLOCK;

@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class ResonatorNameRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    public static Map<BlockPos, String> posName = new HashMap<>();

    @SubscribeEvent
    public static void renderPositionName(RenderLevelStageEvent event) {
        if (posName.isEmpty()) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        PoseStack poseStack = event.getPoseStack();
        var camPos = mc.gameRenderer.getMainCamera().getPosition();
        for (var entry : posName.entrySet()) {
            BlockPos pos = entry.getKey();
            poseStack.pushPose();
            renderPositionName(entry.getValue(), mc.renderBuffers().bufferSource(), poseStack, pos);
            poseStack.popPose();
        }
    }

    public static void renderPositionName(String name, MultiBufferSource bufferSource, PoseStack poseStack,BlockPos blockPos) {
        var camera = mc.gameRenderer.getMainCamera();
        float textWidth = mc.font.width(name) / 2.0f;
        double offX = blockPos.getCenter().x - camera.getPosition().x;
        double offY = blockPos.getCenter().y - camera.getPosition().y + 0.4;
        double offZ = blockPos.getCenter().z - camera.getPosition().z;
        double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
        // 把渲染点拉到相机前 128 米以内，避免 float 精度坍塌造成“中心消失/边缘能显示”的裁剪现象。
        final double MAX_RENDER_DISTANCE = 128;
        final double MIN_RENDER_DISTANCE = 2.0;
        double renderDistance;
        var dir = new Vector3d(offX, offY, offZ).normalize();
        renderDistance = Math.min(distance, MAX_RENDER_DISTANCE);
        renderDistance = Math.max(renderDistance, MIN_RENDER_DISTANCE);
        double rx = dir.x * renderDistance;
        double ry = dir.y * renderDistance;
        double rz = dir.z * renderDistance;
        poseStack.translate((float) rx, (float) ry, (float) rz);

        // 应用相机朝向
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        // 固定大小
        float screenScale = 0.06f * (float) Math.min(distance, MAX_RENDER_DISTANCE);
        if (screenScale < 1f) screenScale = 1f;
        poseStack.scale(screenScale, screenScale, screenScale);
        poseStack.scale(0.033f, 0.033f, 0.033f);
        mc.font.drawInBatch(Component.literal(name), -textWidth, 0,
                FastColor.ABGR32.color(255, 140, 244, 226), false,
                poseStack.last().pose(), bufferSource, Font.DisplayMode.SEE_THROUGH, 0, FULL_BLOCK
        );
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        posName.clear();
    }
}
