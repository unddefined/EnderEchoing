package com.unddefined.enderechoing.client.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EchoSounding {

    public static void render(PoseStack poseStack, MultiBufferSource bufferSource,
                              float partialTicks, float gameTimes, int packedLight) {
        // AFTER_LEVEL 传入的 poseStack 是空的，modelView 只是相机旋转矩阵（无平移）。
        // 因此这里必须像 EchoResponse 一样用"玩家位置 - 相机位置"的相对坐标，才能让波纹
        // 以玩家脚下为中心展开，而不是落在相机（世界原点）脚下，尤其在第三人称/旁观时正确。
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Camera camera = mc.gameRenderer.getMainCamera();
        double offX = 0, offY = -1, offZ = 0;
        if (player != null) {
            Vec3 camPos = camera.getPosition();
            double footX = player.getX();
            double footY = player.getY() + 1;
            double footZ = player.getZ();
            offX = footX - camPos.x;
            offY = footY - camPos.y;
            offZ = footZ - camPos.z;
        }

        poseStack.pushPose();
        // 波纹水平展开在玩家脚下
        poseStack.translate(offX, offY, offZ);
        // 当前时间（每波纹起始时刻不同）
        float gameTime = gameTimes + partialTicks;

        float offset = 2f; // 每个波纹错开起始时间
        float age = (gameTime - offset) % 60f; // 周期性扩散

        float scale2 = 0.2f + age / 2f; // 控制半径增大
        float alpha = Math.max(0f, 1f - age / 60f); // 随半径增大透明度逐渐减小

        poseStack.scale(scale2, 0, scale2); // 缩放波纹平面

        var vc = bufferSource.getBuffer(EchoResponse.WAVE_RENDER_TYPE);
        var mat = poseStack.last().pose();
        int alphaInt = (int) (alpha * 255);

        // 绘制一个平面 quad，包含所有必需的顶点属性
        vc.addVertex(mat, -1f, 0, -1f).setUv(0f, 0f).setColor(41, 223, 235, alphaInt)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        vc.addVertex(mat, 1f, 0, -1f).setUv(1f, 0f).setColor(41, 223, 235, alphaInt)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        vc.addVertex(mat, 1f, 0, 1f).setUv(1f, 1f).setColor(41, 223, 235, alphaInt)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
        vc.addVertex(mat, -1f, 0, 1f).setUv(0f, 1f).setColor(41, 223, 235, alphaInt)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);

        poseStack.popPose();
    }
}
