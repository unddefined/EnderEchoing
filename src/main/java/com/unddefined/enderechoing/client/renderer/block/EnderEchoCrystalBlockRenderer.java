package com.unddefined.enderechoing.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.unddefined.enderechoing.blocks.entity.EnderEchoCrystalBlockEntity;
import com.unddefined.enderechoing.client.model.EnderEchoCrystalBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import static net.minecraft.client.renderer.entity.EnderDragonRenderer.CRYSTAL_BEAM_LOCATION;

public class EnderEchoCrystalBlockRenderer extends GeoBlockRenderer<EnderEchoCrystalBlockEntity> {
    private static final RenderType BEAM = RenderType.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);

    public EnderEchoCrystalBlockRenderer() {
        super(new EnderEchoCrystalBlockModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, EnderEchoCrystalBlockEntity animatable, BakedGeoModel model, @Nullable RenderType renderType,
                               MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                               int packedOverlay, int colour) {
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
        if (animatable.getLevel() == null || animatable.getPlayerUUID().equals(EnderEchoCrystalBlockEntity.nullUUID)) return;

        var tick = animatable.getTick(animatable);
        var player = animatable.getLevel().getPlayerByUUID(animatable.getPlayerUUID());
        var blockPos = animatable.getBlockPos();
        if (player == null || player.distanceToSqr(blockPos.getCenter()) > 16 * 16 || player.getHealth() > player.getMaxHealth()) return;
        poseStack.pushPose();

        float x = (float) -(Mth.lerp(partialTick, player.xo, player.getX()) - 0.5 - blockPos.getX());
        float y = (float) -(Mth.lerp(partialTick, player.yo, player.getY()) - 0.7 - blockPos.getY() + player.getBbHeight() * 0.5);
        float z = (float) -(Mth.lerp(partialTick, player.zo, player.getZ()) - 0.5 - blockPos.getZ());
        poseStack.translate(-x, -y, -z);

        var floatY = model.getBone("membrane").get().getPosY() / 15 + y;
        float f = Mth.sqrt(x * x + z * z);
        float f1 = Mth.sqrt(x * x + floatY * floatY + z * z);
        float f2 = 0.0F - ((float) tick + partialTick) * 0.01F;
        float f3 = f1 / 32.0F - ((float) tick + partialTick) * 0.01F;
        float f4 = 0.0F;
        float f5 = 0.75F;
        float f6 = 0.0F;

        poseStack.pushPose();
        poseStack.translate(0.0F, 1.0F, 0.0F);
        poseStack.mulPose(Axis.YP.rotation((float) (-Math.atan2(z, x)) - ((float) Math.PI / 2F)));
        poseStack.mulPose(Axis.XP.rotation((float) (-Math.atan2(f, floatY)) - ((float) Math.PI / 2F)));

        var vertexconsumer = bufferSource.getBuffer(BEAM);
        var posestack$pose = poseStack.last();
        int color1 = FastColor.ABGR32.color(150, 17, 27, 33);
        int color2 = FastColor.ABGR32.color(150, 44, 242, 255);
        int color3 = FastColor.ABGR32.color(150, 162, 249, 255);

        for (int j = 1; j <= 8; ++j) {
            float f7 = Mth.sin((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.45F;
            float f8 = Mth.cos((float) j * ((float) Math.PI * 2F) / 8.0F) * 0.45F;
            float f9 = (float) j / 8.0F;
            vertexconsumer.addVertex(posestack$pose, f4 * 0.2F, f5 * 0.2F, 0.0F).setColor(color1).setUv(f6, f2).setOverlay(packedOverlay).setLight(packedLight).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
            vertexconsumer.addVertex(posestack$pose, f4, f5, f1).setColor(color3).setUv(f6, f3).setOverlay(packedOverlay).setLight(packedLight).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
            vertexconsumer.addVertex(posestack$pose, f7, f8, f1).setColor(color3).setUv(f9, f3).setOverlay(packedOverlay).setLight(packedLight).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
            vertexconsumer.addVertex(posestack$pose, f7 * 0.2F, f8 * 0.2F, 0.0F).setColor(color1).setUv(f9, f2).setOverlay(packedOverlay).setLight(packedLight).setNormal(posestack$pose, 0.0F, -1.0F, 0.0F);
            f4 = f7;
            f5 = f8;
            f6 = f9;
        }

        poseStack.popPose();
        poseStack.popPose();
    }
}
