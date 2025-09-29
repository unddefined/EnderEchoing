package com.unddefined.enderechoing.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.particles.EchoResponse;
import com.unddefined.enderechoing.client.particles.EchoSounding;
import com.unddefined.enderechoing.network.packet.AddEffectPacket;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
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
import java.util.List;

@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class EchoRenderer {
    public static BlockPos EchoSoundingPos = null;
    public static boolean EchoSoundingExtraRender = false;
    private static int countTicks = 0;
    private static int countdownTicks = 60;
    private static boolean isCounting = false;
    private static boolean isCountingDown = false;
    // 存储从服务端同步过来的传送器位置
    private static List<BlockPos> syncedTeleporterPositions = new ArrayList<>();

    // 更新传送器位置的方法
    public static void updateTeleporterPositions(List<BlockPos> positions) {
        syncedTeleporterPositions = new ArrayList<>(positions);
    }

    @SubscribeEvent
    public static void renderEcho(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (!isCounting && !isCountingDown) return;
        int tick = isCounting ? countTicks : countdownTicks;
        PoseStack PoseStack = new PoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        var PartialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        if (EchoSoundingExtraRender) {
            EchoSounding.render(PoseStack, bufferSource, 0, -1, 0,
                    PartialTicks, tick - 20, 0xF000F0);
        }
        if (tick > 20) {
            //渲染EchoSounding
            EchoSounding.render(PoseStack, bufferSource, 0, -1, 0,
                    PartialTicks, tick - 20, 0xF000F0);
        }
        if (tick > 120) {
            // 渲染EchoResponse
            for (BlockPos pos : syncedTeleporterPositions) {
                if (pos.equals(EchoSoundingPos)) continue;
                // 检查位置是否在渲染范围内
                if (new AABB(camera.getBlockPosition()).inflate(4096).contains(Vec3.atCenterOf(pos))) {
                    Vec3 blockPos = Vec3.atCenterOf(pos);
                    EchoResponse.render(
                            PoseStack, bufferSource,
                            blockPos.x - camera.getPosition().x,
                            blockPos.y - camera.getPosition().y + 1,
                            blockPos.z - camera.getPosition().z,
                            countTicks - 150, isCountingDown, 0xF000F0 // full brightness
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        EchoSoundingPos = null;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (EchoSoundingPos != null) {
            isCounting = true;
            countdownTicks = 60;
        }
        countTicks = isCounting ? countTicks + 1 : 0;
        if (countdownTicks == 0) {
            isCountingDown = false;
            isCounting = false;
            return;
        }
        countdownTicks--;
        if (EchoSoundingPos == null) return;
        if (!new AABB(EchoSoundingPos).inflate(0.6).contains(event.getEntity().blockPosition().getCenter())) {
            // 玩家离开了方块，重置状态
            EchoSoundingPos = null;
            PacketDistributor.sendToServer(new AddEffectPacket(MobEffects.GLOWING, 600));
            isCountingDown = true;
            EchoSoundingExtraRender = false;
        }

    }
}