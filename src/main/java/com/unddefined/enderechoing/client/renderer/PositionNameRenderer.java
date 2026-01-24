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

import java.util.Map;

import static net.minecraft.client.renderer.LightTexture.FULL_BLOCK;

@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class PositionNameRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    public static Map<BlockPos, String> posName = new java.util.HashMap<>();
    @SubscribeEvent
    public static void renderPositionName(RenderLevelStageEvent event) {
        if (posName == null || posName.size() != 1) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        event.getPoseStack().pushPose();
        var pos = posName.keySet().stream().findFirst().get();
        var camPos = mc.gameRenderer.getMainCamera().getPosition();
        event.getPoseStack().translate(pos.getX() + 0.5f - camPos.x, pos.getY() + 1f - camPos.y , pos.getZ() + 0.5f - camPos.z);
        renderPositionName(posName.values().stream().findFirst().get(), mc.renderBuffers().bufferSource(), event.getPoseStack());
        event.getPoseStack().popPose();
    }
    public static void renderPositionName(String name, MultiBufferSource bufferSource, PoseStack poseStack) {
        var camera = mc.gameRenderer.getMainCamera();
        float textWidth = mc.font.width(name) / 2.0f;
        poseStack.scale(0.033f, 0.033f, 0.033f);
        poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        mc.font.drawInBatch(
                Component.literal(name), -textWidth, 0,
                FastColor.ABGR32.color(255, 140, 244, 226), false,
                poseStack.last().pose(), bufferSource,
                Font.DisplayMode.NORMAL, 0, FULL_BLOCK
        );
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {posName.clear();}
}
