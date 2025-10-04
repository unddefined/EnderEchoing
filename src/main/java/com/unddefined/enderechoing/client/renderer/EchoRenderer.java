package com.unddefined.enderechoing.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.particles.EchoResponse;
import com.unddefined.enderechoing.client.particles.EchoResponsing;
import com.unddefined.enderechoing.client.particles.EchoSounding;
import com.unddefined.enderechoing.network.packet.AddEffectPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class EchoRenderer {
    public static BlockPos EchoSoundingPos = null;
    public static boolean EchoSoundingExtraRender = false;
    public static Matrix4f ProjectionMatrix = null;
    private static int countTicks = 0;
    private static int countdownTicks = 60;
    private static int teleportTicks = 0;
    private static boolean isCounting = false;
    private static Matrix4f ModelViewMatrix = null;
    private static Player player = null;
    private static Vec3 targetPos = null;
    // 存储从服务端同步过来的传送器位置
    private static List<BlockPos> syncedTeleporterPositions = new ArrayList<>();

    // 更新传送器位置的方法
    public static void updateTeleporterPositions(List<BlockPos> positions) {syncedTeleporterPositions = new ArrayList<>(positions);}

    @SubscribeEvent
    public static void renderEcho(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (!isCounting) return;
        int tick = countdownTicks < 59 ? countdownTicks : countTicks;
        var PoseStack = event.getPoseStack();
        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        var PartialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        if (Minecraft.getInstance().level == null) return;
        if (EchoSoundingExtraRender) {
            EchoSounding.render(PoseStack, bufferSource, 0, -1, 0,
                    PartialTicks, tick - 20, LightTexture.FULL_BRIGHT);
        }
        if (tick > 20) {
            EchoSounding.render(PoseStack, bufferSource, 0, -1, 0,
                    PartialTicks, tick - 20, LightTexture.FULL_BRIGHT);
        }

        var Camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        var worldPoseStack = new PoseStack();
        worldPoseStack.pushPose();
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().set(ModelViewMatrix);
        RenderSystem.applyModelViewMatrix();
        if (countTicks > 120) {
            // 渲染EchoResponse
            for (BlockPos pos : syncedTeleporterPositions) {
                if (pos.equals(EchoSoundingPos)) continue;
                if (new AABB(Camera.getBlockPosition()).inflate(4096).contains(Vec3.atCenterOf(pos))) {
                    var blockPos = Vec3.atCenterOf(pos);
                    boolean isElementHovering = EchoResponse.render(worldPoseStack, bufferSource, pos, countTicks - 160,
                            countdownTicks < 59);
                    if (isElementHovering && !player.isCurrentlyGlowing()) {
                        targetPos = blockPos;
                        EchoResponsing.render(worldPoseStack, bufferSource, blockPos, ++teleportTicks);
                        if (teleportTicks > 53) {
                            player.teleportTo(blockPos.x, blockPos.y, blockPos.z);
                            teleportTicks = 0;
                        }
                    }
                    if (targetPos != null && targetPos.equals(blockPos) && !isElementHovering) teleportTicks = 0;
                }
            }

        }
        worldPoseStack.popPose();
        bufferSource.endBatch();
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.applyModelViewMatrix();
    }

    @SubscribeEvent
    public static void handleRenderSolidBlocks(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return;
        ModelViewMatrix = event.getModelViewMatrix();
        ProjectionMatrix = event.getProjectionMatrix();
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {EchoSoundingPos = null;}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        player = event.getEntity();
        if (EchoSoundingPos != null) {
            isCounting = true;
            countdownTicks = 60;
        }
        countTicks = isCounting ? countTicks + 1 : 0;
        if (countdownTicks == 0) {
            isCounting = false;
            EchoResponse.activeWavesMap.clear();
            return;
        }
        countdownTicks--;
        if (EchoSoundingPos == null) return;
        if (!new AABB(EchoSoundingPos).inflate(0.6).contains(event.getEntity().blockPosition().getCenter())) {
            // 玩家离开了方块，重置状态
            PacketDistributor.sendToServer(new AddEffectPacket(MobEffects.GLOWING, 600));
            EchoSoundingPos = null;
            EchoSoundingExtraRender = false;
        }

    }


}