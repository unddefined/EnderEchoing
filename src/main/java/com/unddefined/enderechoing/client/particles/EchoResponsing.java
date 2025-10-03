package com.unddefined.enderechoing.client.particles;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.function.Function;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;

@OnlyIn(Dist.CLIENT)
public class EchoResponsing {
    private static final Function<ResourceLocation, RenderType> RESPONSING = Util.memoize((r)-> RenderType.create(
            "ender_echoic_responsing",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(r, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .createCompositeState(true)
    ));

    public static void render(PoseStack poseStack, MultiBufferSource bufferSource,
                              Vec3 blockPos, int ticks) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        double screenHeight = Minecraft.getInstance().getWindow().getHeight();
        double offX = blockPos.x - camera.getPosition().x;
        double offY = blockPos.y - camera.getPosition().y + 1;
        double offZ = blockPos.z - camera.getPosition().z;
        poseStack.pushPose();
        double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
        if (distance > 250000.0D) {
            double offScaler = 250000.0D / distance;
            offX *= offScaler;
            offY *= offScaler;
            offZ *= offScaler;
        }
        poseStack.translate(offX, offY, offZ);
        // apply camera-facing rotation so the quad faces the camera
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        // scale to screen-constant size
        double fov = Minecraft.getInstance().options.fov().get().doubleValue();
        double fovMultiplier = 2.0D * Math.tan(Math.toRadians(fov / 2.0D));
        float screenScale = (float) (distance / screenHeight / fovMultiplier * 160);
        if (screenScale < 1) screenScale = 1.0f;
        poseStack.scale(screenScale, screenScale, 0);

        // 渲染SONIC_BOOM
        poseStack.pushPose();
        int frameCount = 14;
        int frameDuration = 3;
        int frameIndex = frameCount - 1 - (ticks / frameDuration) % frameCount;
        VertexConsumer vc = bufferSource.getBuffer(RESPONSING.apply(
                ResourceLocation.fromNamespaceAndPath("enderechoing", "textures/misc/sonic_boom_" + frameIndex + ".png")));

        Matrix4f mat = poseStack.last().pose();
        // 绘制一个平面 quad，包含所有必需的顶点属性
        vc.addVertex(mat, -1f, -1f, 0f).setUv(0f, 0f).setColor(41, 223, 235, 255)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 1f, 0f);
        vc.addVertex(mat, 1f, -1f, 0f).setUv(1f, 0f).setColor(41, 223, 235, 255)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 1f, 0f);
        vc.addVertex(mat, 1f, 1f, 0f).setUv(1f, 1f).setColor(41, 223, 235, 255)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 1f, 0f);
        vc.addVertex(mat, -1f, 1f, 0f).setUv(0f, 1f).setColor(41, 223, 235, 255)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 1f, 0f);
        poseStack.popPose();

        // pop world pose for quad drawing
        poseStack.popPose();
    }
}
