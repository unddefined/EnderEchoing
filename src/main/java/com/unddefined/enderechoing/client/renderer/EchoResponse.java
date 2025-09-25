package com.unddefined.enderechoing.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class EchoResponse {
    private static final RenderType WAVE_RENDER_TYPE = RenderType.create(
            "ender_echoic_wave",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath("enderechoing", "textures/misc/wave.png"), false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .createCompositeState(true)
    );
    public static void render(PoseStack poseStack,
                              MultiBufferSource bufferSource,
                              double centerX, double centerY, double centerZ,
                              float partialTicks, float gameTimes,int packedLight) {
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, centerZ);
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        poseStack.mulPose(Axis.YN.rotationDegrees(camera.getYRot()));
        poseStack.mulPose(Axis.XN.rotationDegrees(-camera.getXRot()));
        // 当前时间（每波纹起始时刻不同）
        float gameTime = gameTimes + partialTicks;
//TODO:随距离缩放
        // 渲染三个波纹
        for (int i = 0; i < 3; i++) {
            float offset = i * 30f; // 每个波纹错开起始时间
            float age = (gameTime - offset) % 45f; // 周期性扩散
            if (age < 0) age += 60f;

            float scale = 0.2f + age / 30f; // 控制半径增大
            float alpha = Math.max(0f, 1f - age / 30f); // 随半径增大透明度逐渐减小

            poseStack.pushPose();
            poseStack.scale(scale, scale, 0); // 缩放波纹平面

            VertexConsumer vc = bufferSource.getBuffer(WAVE_RENDER_TYPE);
            Matrix4f mat = poseStack.last().pose();
            int alphaInt = (int) (alpha * 255);

            // 绘制一个平面 quad，包含所有必需的顶点属性
            vc.addVertex(mat, -1f, -1f, 0f).setUv(0f, 0f).setColor(41, 223, 235, alphaInt)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal( 0f, 1f, 0f);
            vc.addVertex(mat, 1f, -1f, 0f).setUv(1f, 0f).setColor(41, 223, 235, alphaInt)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal( 0f, 1f, 0f);
            vc.addVertex(mat, 1f, 1f, 0f).setUv(1f, 1f).setColor(41, 223, 235, alphaInt)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal( 0f, 1f, 0f);
            vc.addVertex(mat, -1f, 1f, 0f).setUv(0f, 1f).setColor(41, 223, 235, alphaInt)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal( 0f, 1f, 0f);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
