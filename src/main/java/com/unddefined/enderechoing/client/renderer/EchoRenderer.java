package com.unddefined.enderechoing.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.unddefined.enderechoing.server.registry.MobEffectRegistry.SCULK_VEIL;

@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class EchoRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Map<BlockPos, EchoResponse> echoMap = new HashMap<>();
    public static BlockPos EchoSoundingPos = null;
    public static boolean targetPreseted = false;
    public static BlockPos targetPos = null;
    public static List<BlockPos> syncedTeleporterPositions = new ArrayList<>();
    public static Map<BlockPos, String> MarkedPositionNames = new HashMap<>();
    private static int countTicks = 0;
    private static int countdownTicks = 60;
    private static int sculkveilCountTicks = -43;
    private static int teleportTicks = 0;
    private static int responseTime = 120;
    private static boolean isCounting = false;
    private static boolean isTeleporting = false;

    //TODO:兼容iris
    @SubscribeEvent
    public static void renderEcho(RenderLevelStageEvent event) {
        if (mc.player == null) return;
        float PartialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        SculkVeilRenderer.updateFadeProgress(mc.player.hasEffect(SCULK_VEIL), PartialTicks);
        if (!isCounting && SculkVeilRenderer.fadeProgress == 0f) return;
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;

        int tick = countdownTicks < 59 ? countdownTicks : countTicks;
        var PoseStack = event.getPoseStack();
        var bufferSource = mc.renderBuffers().bufferSource();

        var originalTarget = mc.getMainRenderTarget();
        if (SculkVeilRenderer.fadeProgress != 0f)
            SculkVeilRenderer.renderSculkVeil(sculkveilCountTicks, PartialTicks, event.getModelViewMatrix(), event.getProjectionMatrix());
        originalTarget.bindWrite(false);

        RenderSystem.disableDepthTest();

        if (targetPreseted) {
            EchoSounding.render(PoseStack, bufferSource, PartialTicks, tick - 20, LightTexture.FULL_BRIGHT);
            //定向传送
            if (targetPos != null && echoMap.containsKey(targetPos)) {
                echoMap.getOrDefault(targetPos, null)
                        .render(mc.player, PoseStack, bufferSource, teleportTicks - 80, false, null);
                if (teleportTicks > 60) EchoResponsing.render(PoseStack, bufferSource, targetPos, teleportTicks);
            }
        }

        if (tick > 20) EchoSounding.render(PoseStack, bufferSource, PartialTicks, tick - 20, LightTexture.FULL_BRIGHT);

        if (!targetPreseted && countTicks > responseTime && !echoMap.isEmpty()) {
            // 渲染EchoResponse
            echoMap.forEach((p, e) -> {
                boolean isElementHovering = e.render(mc.player, PoseStack, bufferSource, countTicks - 40 - responseTime,
                        countdownTicks < 59, MarkedPositionNames.getOrDefault(p, null));
                if (isElementHovering && !mc.player.isCurrentlyGlowing()) EchoResponsing.render(PoseStack, bufferSource, p, teleportTicks);
            });
        }

        bufferSource.endBatch();
        RenderSystem.enableDepthTest();
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {reset();echoMap.clear();}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (EchoSoundingPos != null && EchoSoundingPos.equals(BlockPos.ZERO)) EchoSoundingPos = null;
        if (targetPos != null && targetPos.equals(BlockPos.ZERO)) targetPos = null;
        var player = event.getEntity();
        if (SculkVeilRenderer.fadeProgress != 0f) sculkveilCountTicks++;
        else sculkveilCountTicks = -43;
        if (teleportTicks > 82 && !player.isCurrentlyGlowing() && !isTeleporting) {
            PacketDistributor.sendToServer(new TeleportRequestPacket(targetPos));
            isTeleporting = true;
        }
        if (targetPos != null && targetPreseted) {
            echoMap.putIfAbsent(targetPos, new EchoResponse(targetPos));
            teleportTicks++;
        }
        if (EchoSoundingPos != null) {
            isCounting = true;
            countdownTicks = 60;
            if (echoMap.isEmpty() && !syncedTeleporterPositions.isEmpty()) {
                List<Double> disstances = new ArrayList<>();
                syncedTeleporterPositions.forEach(pos -> disstances.add(EchoSoundingPos.distSqr(pos)));
                if (disstances.stream().max(Double::compareTo).get() <= 64*64) responseTime = 30;
                else responseTime = 120;

                for (BlockPos pos : syncedTeleporterPositions) {
                    if (pos.equals(EchoSoundingPos) && responseTime != 30) continue;
                    if (!new AABB(EchoSoundingPos).inflate(4096).contains(Vec3.atCenterOf(pos))) continue;
                    echoMap.putIfAbsent(pos, new EchoResponse(pos));
                }
            }
        }
        echoMap.forEach((p, e) -> {
            if (e.isElementHovering) {
                teleportTicks++;
                e.hoveringTicks++;
                targetPos = p;
                if (teleportTicks > 40 && !player.isCurrentlyGlowing() && !isTeleporting) {
                    PacketDistributor.sendToServer(new TeleportRequestPacket(targetPos));
                    isTeleporting = true;
                }
            }
            if (!targetPreseted && countTicks > responseTime && targetPos != null && targetPos.equals(p) && !e.isElementHovering) teleportTicks = 0;
        });
        countTicks = isCounting ? countTicks + 1 : 0;
        if (countdownTicks == 0) {
            isCounting = false;
            echoMap.clear();
            return;
        }
        countdownTicks--;
        if (EchoSoundingPos == null) return;
        // 玩家离开了方块，重置状态
        if (!new AABB(EchoSoundingPos).intersects(player.getBoundingBox())) {
            if (responseTime != 30) PacketDistributor.sendToServer(new AddEffectPacket(MobEffects.GLOWING, 600));
            reset();
        }
    }

    private static void reset() {
        EchoSoundingPos = null;
        targetPreseted = false;
        targetPos = null;
        teleportTicks = 0;
        sculkveilCountTicks = -43;
        isTeleporting = false;
    }
}