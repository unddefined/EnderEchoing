package com.unddefined.enderechoing.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;

public class EchoSounding {

    public static void render(PoseStack poseStack, MultiBufferSource bufferSource,
                              double centerX, double centerY, double centerZ,
                              float partialTicks, float gameTimes, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, centerZ);
        // 当前时间（每波纹起始时刻不同）
        float gameTime = gameTimes + partialTicks;

        float offset = 2f; // 每个波纹错开起始时间
        float age = (gameTime - offset) % 60f; // 周期性扩散
        if (age < 0) age += 60f;

        float scale2 = 0.2f + age / 2f; // 控制半径增大
        float alpha = Math.max(0f, 1f - age / 60f); // 随半径增大透明度逐渐减小

        poseStack.scale(scale2, 0, scale2); // 缩放波纹平面

        VertexConsumer vc = bufferSource.getBuffer(EchoResponse.WAVE_RENDER_TYPE);
        Matrix4f mat = poseStack.last().pose();
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
