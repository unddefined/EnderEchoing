package com.unddefined.enderechoing.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.particles.EchoResponse;
import com.unddefined.enderechoing.client.particles.EchoResponsing;
import com.unddefined.enderechoing.client.particles.EchoSounding;
import com.unddefined.enderechoing.network.packet.TeleportRequestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
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

import static com.unddefined.enderechoing.Config.EchoSoundingDistance;
import static com.unddefined.enderechoing.server.registry.MobEffectRegistry.SCULK_VEIL;

@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class EchoRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Map<BlockPos, EchoResponse> echoMap = new HashMap<>();
    public static BlockPos EchoSoundingPos = null;
    public static boolean targetPreseted = false;
    public static GlobalPos targetPos = null;
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
            if (targetPos != null && echoMap.containsKey(targetPos.pos())) {
                echoMap.getOrDefault(targetPos.pos(), null)
                        .render(mc.player, PoseStack, bufferSource, teleportTicks - 80, false, null);
                if (teleportTicks > 60) EchoResponsing.render(PoseStack, bufferSource, targetPos.pos(), teleportTicks);
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
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        reset();
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (EchoSoundingPos != null && EchoSoundingPos.equals(BlockPos.ZERO)) reset();
        if (targetPos != null && targetPos.pos().equals(BlockPos.ZERO)) targetPos = null;
        var player = event.getEntity();
        var level = player.level();
        if (SculkVeilRenderer.fadeProgress != 0f) sculkveilCountTicks++;
        else sculkveilCountTicks = -43;
        if (teleportTicks > 82 && !player.isCurrentlyGlowing() && !isTeleporting) {
            PacketDistributor.sendToServer(new TeleportRequestPacket(targetPos));
            echoMap.remove(targetPos.pos());
            isTeleporting = true;
        }
        if (targetPos != null && targetPreseted && !isTeleporting) {
            if (level.dimension().equals(targetPos.dimension()))
                echoMap.putIfAbsent(targetPos.pos(), new EchoResponse(targetPos.pos()));
            teleportTicks++;
        }
        if (EchoSoundingPos != null) {
            isCounting = true;
            countdownTicks = 60;
            if (player.hasEffect(SCULK_VEIL)) responseTime = 120;
            else responseTime = 30;
            if (echoMap.isEmpty() && !syncedTeleporterPositions.isEmpty()) {
                for (BlockPos pos : syncedTeleporterPositions) {
                    if (pos.equals(EchoSoundingPos)) continue;
                    if (!new AABB(EchoSoundingPos).inflate(EchoSoundingDistance.get()).contains(Vec3.atCenterOf(pos))) continue;
                    echoMap.putIfAbsent(pos, new EchoResponse(pos));
                }
            }
        }
        // The callback may add/remove entries (see the teleport branch below),
        // so iterate over a snapshot instead of modifying HashMap while it is
        // being traversed.
        new HashMap<>(echoMap).forEach((p, e) -> {
            if (e.isElementHovering) {
                teleportTicks++;
                e.hoveringTicks++;
                targetPos = new GlobalPos(level.dimension(), p);
                if (teleportTicks > 40 && !player.isCurrentlyGlowing() && !isTeleporting) {
                    isTeleporting = true;
                    echoMap.putIfAbsent(EchoSoundingPos, new EchoResponse(EchoSoundingPos));
                    echoMap.remove(targetPos.pos());
                    PacketDistributor.sendToServer(new TeleportRequestPacket(targetPos));
                }
            }
            if (!targetPreseted && countTicks > responseTime && targetPos != null && targetPos.pos().equals(p) && !e.isElementHovering)
                teleportTicks = 0;
        });
        countTicks = isCounting ? countTicks + 1 : 0;
        if (countdownTicks == 0) {
            isCounting = false;
            echoMap.clear();
            reset();
            return;
        }
        countdownTicks--;
        if (EchoSoundingPos == null) return;
        // 玩家离开了方块，重置状态
        if (!new AABB(EchoSoundingPos).intersects(player.getBoundingBox())) reset();
    }

    private static void reset() {
        EchoSoundingPos = null;
        syncedTeleporterPositions.clear();
        targetPreseted = false;
        targetPos = null;
        teleportTicks = 0;
//        sculkveilCountTicks = -43;
        isTeleporting = false;
    }
}
