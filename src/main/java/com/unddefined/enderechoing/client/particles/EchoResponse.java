package com.unddefined.enderechoing.client.particles;

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
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EchoResponse {
    public static final RenderType WAVE_RENDER_TYPE = RenderType.create(
            "ender_echoic_wave",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            false,
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
    private static final List<Float> activeWaves = new ArrayList<>();

    public static void render(PoseStack poseStack, MultiBufferSource bufferSource,
                              Vec3 blockPos, float ticks, boolean isCountingDown, int packedLight) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        double screenHeight = Minecraft.getInstance().getWindow().getHeight();
        double offX = blockPos.x - camera.getPosition().x;
        double offY = blockPos.y - camera.getPosition().y;
        double offZ = blockPos.z - camera.getPosition().z;
        double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
        if (distance > 250000.0D) {
            double offScaler = 250000.0D / distance;
            offX *= offScaler;
            offY *= offScaler;
            offZ *= offScaler;
        }

        double fov = Minecraft.getInstance().options.fov().get().doubleValue();
        double fovMultiplier = 2.0D * Math.tan(Math.toRadians(fov / 2.0D));
        float screenScale = (float) (distance / screenHeight / fovMultiplier * 160);
        if (screenScale < 1) screenScale = 1.0f;

        poseStack.pushPose();
        poseStack.translate(offX, offY + 1, offZ);
        // apply camera-facing rotation so the quad faces the camera
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        // scale to screen-constant size
        poseStack.scale(screenScale, screenScale, 0);

        // 每隔30tick生成一个新的波纹
        if (!isCountingDown && ticks % 30 == 0) activeWaves.add(ticks);
        Iterator<Float> it = activeWaves.iterator();

        // 渲染三个波纹
        while (it.hasNext()) {
            float offset = it.next();
            float age = ticks - offset; // 周期性扩散
            if (age > 120) {
                it.remove(); // 生命周期结束
                continue;
            }
            float scale2 = 0.2f + age / 54f; // 控制半径增大
            float alpha = Math.max(0f, 1f - age / 60f); // 随半径增大透明度逐渐减小
            poseStack.pushPose();
            poseStack.scale(scale2, scale2, 0); // 缩放波纹平面
            VertexConsumer vc = bufferSource.getBuffer(WAVE_RENDER_TYPE);
            Matrix4f mat = poseStack.last().pose();
            int alphaInt = (int) (alpha * 255);

            // 绘制一个平面 quad，包含所有必需的顶点属性
            vc.addVertex(mat, -1f, -1f, 0f).setUv(0f, 0f).setColor(41, 223, 235, alphaInt)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
            vc.addVertex(mat, 1f, -1f, 0f).setUv(1f, 0f).setColor(41, 223, 235, alphaInt)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
            vc.addVertex(mat, 1f, 1f, 0f).setUv(1f, 1f).setColor(41, 223, 235, alphaInt)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
            vc.addVertex(mat, -1f, 1f, 0f).setUv(0f, 1f).setColor(41, 223, 235, alphaInt)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(0f, 1f, 0f);
            poseStack.popPose();
        }
        // pop world pose for quad drawing
        poseStack.popPose();
    }
}