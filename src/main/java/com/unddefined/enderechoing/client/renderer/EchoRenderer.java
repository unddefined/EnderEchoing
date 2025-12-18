package com.unddefined.enderechoing.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.particles.EchoResponse;
import com.unddefined.enderechoing.client.particles.EchoResponsing;
import com.unddefined.enderechoing.client.particles.EchoSounding;
import com.unddefined.enderechoing.network.packet.AddEffectPacket;
import com.unddefined.enderechoing.network.packet.TeleportRequestPacket;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.unddefined.enderechoing.server.registry.MobEffectRegistry.SCULK_VEIL;

@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class EchoRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Map<Vec3, Vec3> shiftPosMap = new HashMap<>();
    public static BlockPos EchoSoundingPos = null;
    public static boolean EchoSoundingExtraRender = false;
    public static Vec3 targetPos = null;
    public static List<BlockPos> syncedTeleporterPositions = new ArrayList<>();
    public static Map<BlockPos, String> MarkedPositionNames = new HashMap<>();
    private static Matrix4f ProjectionMatrix = null;
    private static Matrix4f ModelViewMatrix = null;
    private static int countTicks = 0;
    private static int countdownTicks = 60;
    private static int sculkveilCountTicks = -80;
    private static int teleportTicks = 0;
    private static boolean isCounting = false;
    private static Player player = null;

    //TODO:兼容iris
    @SubscribeEvent
    public static void renderEcho(RenderLevelStageEvent event) {
        var PartialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        SculkVeilRenderer.updateFadeProgress(player.hasEffect(SCULK_VEIL), PartialTicks);
        if (!isCounting && SculkVeilRenderer.fadeProgress == 0f) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;

        int tick = countdownTicks < 59 ? countdownTicks : countTicks;
        var Camera = mc.gameRenderer.getMainCamera();
        var PoseStack = new PoseStack();
        var bufferSource = mc.renderBuffers().bufferSource();

        var originalTarget = mc.getMainRenderTarget();
        if (SculkVeilRenderer.fadeProgress != 0f)
            SculkVeilRenderer.renderSculkVeil(sculkveilCountTicks++, PartialTicks, ModelViewMatrix, ProjectionMatrix);
        else sculkveilCountTicks = -80;
        originalTarget.bindWrite(false);

        RenderSystem.disableDepthTest();

        if (EchoSoundingExtraRender) {
            EchoSounding.render(PoseStack, bufferSource, 0, -1, 0,
                    PartialTicks, tick - 20, LightTexture.FULL_BRIGHT);
            //定向传送
            if (targetPos != null) {
                EchoResponse.render(PoseStack, bufferSource, targetPos, ++teleportTicks - 80, false, null);
                if (teleportTicks > 60) EchoResponsing.render(PoseStack, bufferSource, targetPos, teleportTicks);
                if (teleportTicks > 110) PacketDistributor.sendToServer(new TeleportRequestPacket(targetPos));
            }
        }

        if (tick > 20) {
            EchoSounding.render(PoseStack, bufferSource, 0, -1, 0,
                    PartialTicks, tick - 20, LightTexture.FULL_BRIGHT);
        }

        if (countTicks > 120) {
            // 渲染EchoResponse
            for (BlockPos pos : syncedTeleporterPositions) {
                if (pos.equals(EchoSoundingPos)) continue;
                if (!new AABB(Camera.getBlockPosition()).inflate(4096).contains(Vec3.atCenterOf(pos))) continue;
                var blockPos = Vec3.atCenterOf(pos);
                String posName = MarkedPositionNames.getOrDefault(pos, null);
                // 使用 Shift 键触发随机偏移，以避免多个传送点渲染重叠
                if (player.isShiftKeyDown() && !shiftPosMap.containsKey(blockPos) && EchoSoundingPos != null) {
                    int shiftInt = Math.max(pos.distManhattan(EchoSoundingPos), 6);
                    shiftPosMap.put(blockPos, blockPos.add(0, player.getRandom().nextInt(shiftInt) - (double) shiftInt / 2, 0));
                }
                Vec3 shiftPos = shiftPosMap.getOrDefault(blockPos, blockPos);
                boolean isElementHovering = EchoResponse.render(PoseStack, bufferSource, shiftPos, countTicks - 160,
                        countdownTicks < 59, posName);
                if (isElementHovering && !player.isCurrentlyGlowing()) {
                    targetPos = blockPos;
                    EchoResponsing.render(PoseStack, bufferSource, blockPos, ++teleportTicks);
                    if (teleportTicks > 53) PacketDistributor.sendToServer(new TeleportRequestPacket(targetPos));
                }
                if (targetPos != null && targetPos.equals(blockPos) && !isElementHovering) teleportTicks = 0;
            }
        }

        bufferSource.endBatch();
        RenderSystem.enableDepthTest();
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
        if (!player.isShiftKeyDown() && !shiftPosMap.isEmpty()) shiftPosMap.clear();
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
        // 玩家离开了方块，重置状态
        if (!new AABB(EchoSoundingPos).inflate(0.6).contains(event.getEntity().blockPosition().getCenter())) {
            PacketDistributor.sendToServer(new AddEffectPacket(MobEffects.GLOWING, 600));
            EchoSoundingPos = null;
            EchoSoundingExtraRender = false;
            targetPos = null;
            teleportTicks = 0;
        }
    }
}