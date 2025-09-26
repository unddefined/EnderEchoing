package com.unddefined.enderechoing.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.renderer.EchoResponse;
import com.unddefined.enderechoing.client.renderer.EchoSounding;
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
public class ClientEvent {
    public static BlockPos EchoSoundingPos = null;
    public static boolean isStepOn = false;
    public static boolean EffectAdded = false;
    public static int countdownTicks = 0;
    public static boolean isCounting = false;
    // 存储从服务端同步过来的传送器位置
    private static List<BlockPos> syncedTeleporterPositions = new ArrayList<>();

    // 更新传送器位置的方法
    public static void updateTeleporterPositions(List<BlockPos> positions) {
        syncedTeleporterPositions = new ArrayList<>(positions);
    }

    @SubscribeEvent
    public static void renderEcho(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        if (EchoSoundingPos == null) return;
        PoseStack PoseStack = new PoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        if (countdownTicks > 20) {
            //渲染EchoSounding
            EchoSounding.render(PoseStack, bufferSource, 0, -1, 0,
                    event.getPartialTick().getGameTimeDeltaTicks(), countdownTicks - 20, 0xF000F0);
        }
        if (countdownTicks > 120) {
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
                            event.getPartialTick().getGameTimeDeltaTicks(),
                            countdownTicks - 160, 0xF000F0 // full brightness
                    );
                }
            }
        }

    }

//    @SubscribeEvent
//    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
//        // 为玩家渲染器添加影匿渲染层（有bug）
//        event.getSkins().forEach((skin) -> {
//            EntityRenderer<? extends Player> playerRenderer = event.getSkin(skin);
//            if (playerRenderer instanceof PlayerRenderer renderer) {
//                renderer.addLayer(new SculkVeilLayer(renderer));
//            }
//        });
//    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        // 玩家登出时重置传送数据
        isStepOn = false;
        EchoSoundingPos = null;
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (isStepOn) isCounting = true;
        if (EchoSoundingPos == null) isCounting = false;
        countdownTicks = isCounting ? countdownTicks + 1 : 0;
        if (!isStepOn || EchoSoundingPos == null) return;
        if (!new AABB(EchoSoundingPos).inflate(0.6).contains(event.getEntity().blockPosition().getCenter())) {
            // 玩家离开了方块，重置状态
            isStepOn = false;
            EchoSoundingPos = null;
            PacketDistributor.sendToServer(new AddEffectPacket(MobEffects.GLOWING, 300));
            EffectAdded = false;
        }

    }
}