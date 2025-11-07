package com.unddefined.enderechoing.client.particles;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.*;

import static net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;

@OnlyIn(Dist.CLIENT)
public class EchoResponse {
    public static final RenderType WAVE_RENDER_TYPE = RenderType.create(
            "ender_echoic_wave",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath("enderechoing", "textures/misc/wave.png"), false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .createCompositeState(true)
    );

    public static final Map<Vec3, List<Integer>> activeWavesMap = new HashMap<>();
    private static int hoveringTicks = -31;
    private static Vec3 targetPos = null;

    private static List<Integer> getActiveWavesForPosition(Vec3 pos) {
        return activeWavesMap.computeIfAbsent(pos, blockPos -> new ArrayList<>());
    }

    public static boolean render(PoseStack poseStack, MultiBufferSource bufferSource,
                                 Vec3 blockPos, int ticks, boolean isCountingDown, String posName) {
        var activeWaves = getActiveWavesForPosition(blockPos);
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        double screenHeight = Minecraft.getInstance().getWindow().getHeight();
        double offX = blockPos.x - camera.getPosition().x;
        double offY = blockPos.y - camera.getPosition().y + 1;
        double offZ = blockPos.z - camera.getPosition().z;
        double distance = Math.sqrt(offX * offX + offY * offY + offZ * offZ);
        if (distance > 250000.0D) {
            double offScaler = 250000.0D / distance;
            offX *= offScaler;
            offY *= offScaler;
            offZ *= offScaler;
        }
        poseStack.pushPose();
        poseStack.translate(offX, offY, offZ);
        // 应用相机朝向
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        // 固定大小
        double fov = Minecraft.getInstance().options.fov().get().doubleValue();
        double fovMultiplier = 2.0D * Math.tan(Math.toRadians(fov / 2.0D));
        float screenScale = (float) (distance / screenHeight / fovMultiplier * 160);
        if (screenScale < 1) screenScale = 1.0f;
        poseStack.scale(screenScale, screenScale, 0);
        // 当玩家准星看向那个位置时高亮波纹，并使得波纹由大变小
        var dir = new Vector3f((float) (offX / distance), (float) (offY / distance), (float) (offZ / distance));
        boolean isElementHovering = camera.getLookVector().dot(dir) >= 0.999f;
        if (isElementHovering) {
            targetPos = blockPos;
            ticks = hoveringTicks++;
            if (hoveringTicks == -40) {
                activeWaves.clear();
                activeWaves.add(0);
                activeWaves.add(40);
            }
        } else if (targetPos != null && targetPos.equals(blockPos)) hoveringTicks = -41;

        if (!isElementHovering) {
            // 每隔30tick生成一个新的波纹
            if (ticks < 0 && ticks % 30 == 0) activeWaves.add(-ticks);
            else if (!isCountingDown && ticks % 30 == 0) activeWaves.add(ticks);
        }

        // 渲染三个波纹
        Iterator<Integer> it = activeWaves.iterator();
        while (it.hasNext()) {
            int age = ticks - it.next(); // 周期性扩散
            if (age > 120) {
                it.remove(); // 生命周期结束
                continue;
            }
            float scale2 = Math.abs(0.2f + age / 54f); // 控制半径增大
            float alpha = Math.max(0f, 1f - age / 60f); // 随半径增大透明度逐渐减小
            poseStack.pushPose();
            poseStack.scale(scale2, scale2, 0); // 缩放波纹平面
            var vertexConsumer = bufferSource.getBuffer(WAVE_RENDER_TYPE);
            var matrix4f = poseStack.last().pose();
            int color = isElementHovering ? FastColor.ABGR32.color((int) (alpha * 255), 140, 244, 226)
                    : FastColor.ABGR32.color((int) (alpha * 255 * Math.max(0.1, (1 - (distance / 4096)))), 41, 223, 235);
            // 绘制一个平面 quad，包含所有必需的顶点属性
            vertexConsumer.addVertex(matrix4f, -1f, -1f, 0f).setUv(0f, 0f).setColor(color)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 1f, 0f);
            vertexConsumer.addVertex(matrix4f, 1f, -1f, 0f).setUv(1f, 0f).setColor(color)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 1f, 0f);
            vertexConsumer.addVertex(matrix4f, 1f, 1f, 0f).setUv(1f, 1f).setColor(color)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 1f, 0f);
            vertexConsumer.addVertex(matrix4f, -1f, 1f, 0f).setUv(0f, 1f).setColor(color)
                    .setOverlay(OverlayTexture.NO_OVERLAY).setLight(FULL_BRIGHT).setNormal(0f, 1f, 0f);
            poseStack.popPose();
        }

        // 渲染位置名称
        if (posName != null && ticks > 0) {
            poseStack.pushPose();
            // 渲染文本
            Font font = Minecraft.getInstance().font;
            float textWidth = font.width(posName) / 2.0f;
            poseStack.translate(0, -0.13f, 0);
            poseStack.scale(0.033f, 0.033f, 0.033f);
            font.drawInBatch(
                    Component.literal(posName),
                    -textWidth,
                    0,
                    FastColor.ABGR32.color(255, 140, 244, 226),
                    false,
                    poseStack.last().pose(),
                    bufferSource,
                    Font.DisplayMode.SEE_THROUGH,
                    0,
                    FULL_BRIGHT
            );
            poseStack.popPose();
        }
        poseStack.popPose();
        return isElementHovering;
    }
}