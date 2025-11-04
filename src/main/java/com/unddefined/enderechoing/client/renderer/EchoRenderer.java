package com.unddefined.enderechoing.client.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.particles.EchoResponse;
import com.unddefined.enderechoing.client.particles.EchoResponsing;
import com.unddefined.enderechoing.client.particles.EchoSounding;
import com.unddefined.enderechoing.effects.SculkVeilEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.joml.Matrix4f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.unddefined.enderechoing.client.EnderEchoingClient.sculkVeilPostChain;

@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class EchoRenderer {
    private static final Minecraft mc = Minecraft.getInstance();
    public static BlockPos EchoSoundingPos = null;
    public static boolean EchoSoundingExtraRender = false;
    private static Matrix4f ProjectionMatrix = null;
    private static Matrix4f ModelViewMatrix = null;
    private static int countTicks = 0;
    private static int countdownTicks = 60;
    private static int teleportTicks = 0;
    private static boolean isCounting = false;
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
        if (mc.level == null) return;
        int tick = countdownTicks < 59 ? countdownTicks : countTicks;
        var Camera = mc.gameRenderer.getMainCamera();
        var PoseStack = event.getPoseStack();
        var bufferSource = mc.renderBuffers().bufferSource();
        var PartialTicks = event.getPartialTick().getGameTimeDeltaTicks();
        var projectionMatrixBU = RenderSystem.getProjectionMatrix();
        var vertexSortingBU = RenderSystem.getVertexSorting();
        RenderSystem.getProjectionMatrix().set(ProjectionMatrix);
        RenderSystem.getModelViewStack().pushMatrix();
        RenderSystem.getModelViewStack().set(ModelViewMatrix);
        RenderSystem.applyModelViewMatrix();

        sculkVeilPostChain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
        sculkVeilPostChain.process(PartialTicks);
        applyUniforms(Camera.getPosition());

        RenderSystem.disableDepthTest();

        if (EchoSoundingExtraRender) {
            EchoSounding.render(PoseStack, bufferSource, 0, -1, 0,
                    PartialTicks, tick - 20, LightTexture.FULL_BRIGHT);
        }

        if (tick > 20) {
            EchoSounding.render(PoseStack, bufferSource, 0, -1, 0,
                    PartialTicks, tick - 20, LightTexture.FULL_BRIGHT);
        }

        if (countTicks > 120) {
            // 渲染EchoResponse
            for (BlockPos pos : syncedTeleporterPositions) {
                if (pos.equals(EchoSoundingPos)) continue;
                if (new AABB(Camera.getBlockPosition()).inflate(4096).contains(Vec3.atCenterOf(pos))) {
                    var blockPos = Vec3.atCenterOf(pos);
                    boolean isElementHovering = EchoResponse.render(PoseStack, bufferSource, pos, countTicks - 160,
                            countdownTicks < 59);
                    if (isElementHovering && !player.isCurrentlyGlowing()) {
                        targetPos = blockPos;
                        EchoResponsing.render(PoseStack, bufferSource, blockPos, ++teleportTicks);
                        if (teleportTicks > 53) {
                            player.teleportTo(blockPos.x, blockPos.y, blockPos.z);
                            teleportTicks = 0;
                            System.out.println("teleport");
                        }
                    }
                    if (targetPos != null && targetPos.equals(blockPos) && !isElementHovering) teleportTicks = 0;
                }
            }

        }

        bufferSource.endBatch();
        RenderSystem.getModelViewStack().popMatrix();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(projectionMatrixBU, vertexSortingBU);
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
//            PacketDistributor.sendToServer(new AddEffectPacket(MobEffects.GLOWING, 600));
            EchoSoundingPos = null;
            EchoSoundingExtraRender = false;
            SculkVeilEffect.ParticlesAdded = false;
        }

    }

    private static Field findPassesField() {
        try {
            return ObfuscationReflectionHelper.findField(PostChain.class, "passes");
        } catch (ObfuscationReflectionHelper.UnableToFindFieldException var3) {
            try {
                return ObfuscationReflectionHelper.findField(PostChain.class, "passes");
            } catch (ObfuscationReflectionHelper.UnableToFindFieldException var2) {
                LogUtils.getLogger().info("Unable to find passes field on PostChain using Mojmap or SRG identifiers", var2);
                return null;
            }
        }
    }

    private static List<PostPass> getPasses() {
        if (sculkVeilPostChain == null) {
            return Collections.emptyList();
        } else if (findPassesField() == null) {
            return Collections.emptyList();
        } else {
            try {
                Object value = findPassesField().get(sculkVeilPostChain);
                if (value instanceof List) {
                    return (List<PostPass>) value;
                }
                LogUtils.getLogger().error("Orbital railgun post chain passes had unexpected type: {}", value == null ? "null" : value.getClass().getName());
            } catch (IllegalAccessException var3) {
                LogUtils.getLogger().error("Failed to access orbital railgun post chain passes", var3);
                return Collections.emptyList();
            }

            return Collections.emptyList();
        }
    }

    private static void applyUniforms(Vec3 cameraPos) {
        int GameTime = countdownTicks < 59 ? countdownTicks : countTicks - 50;
        List<PostPass> passes = getPasses();
        if (!passes.isEmpty()) {

            RenderTarget renderTarget = mc.getMainRenderTarget();
            float width = renderTarget.width > 0 ? (float) renderTarget.width : (float) renderTarget.viewWidth;
            float height = renderTarget.height > 0 ? (float) renderTarget.height : (float) renderTarget.viewHeight;
            Matrix4f modelMatrix = new Matrix4f().translation((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z).scale(8);
            Iterator<PostPass> var16 = passes.iterator();

            while (true) {
                if (!var16.hasNext()) return;
                var pass = var16.next();
                var effect = pass.getEffect();

                effect.safeGetUniform("ModelMatrix").set(modelMatrix);
                effect.safeGetUniform("ModelViewMat").set(ModelViewMatrix);
                effect.safeGetUniform("InverseProjectionMatrix").set(ProjectionMatrix.invert());
                effect.safeGetUniform("InverseModelViewMatrix").set(ModelViewMatrix.invert());
                effect.safeGetUniform("CameraPos").set(cameraPos.toVector3f());
                effect.safeGetUniform("GameTime").set((float) GameTime);
                effect.setSampler("DepthSampler", pass.inTarget::getDepthTextureId);
            }
        }
    }
}