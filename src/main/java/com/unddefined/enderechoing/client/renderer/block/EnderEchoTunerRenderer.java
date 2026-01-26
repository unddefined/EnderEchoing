package com.unddefined.enderechoing.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.unddefined.enderechoing.blocks.EnderEchoTunerBlock;
import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import com.unddefined.enderechoing.client.model.EnderEchoTunerModel;
import com.unddefined.enderechoing.client.renderer.ResonatorNameRenderer;
import com.unddefined.enderechoing.client.renderer.layer.EnderEchoTunerLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EnderEchoTunerRenderer extends GeoBlockRenderer<EnderEchoTunerBlockEntity> {
    public EnderEchoTunerRenderer() {
        super(new EnderEchoTunerModel());
        addRenderLayer(new EnderEchoTunerLayer(this));
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        switch (facing) {
            case UP, SOUTH, NORTH, WEST, EAST -> poseStack.translate(0, 0, 0);
            case DOWN -> {
                poseStack.mulPose(Axis.XN.rotationDegrees(180));
                poseStack.translate(0, -1.0, 0);
            }
        }
    }

    @Override
    protected Direction getFacing(EnderEchoTunerBlockEntity block) {
        BlockState blockState = block.getBlockState();
        // 检查我们自己的FACING属性
        if (blockState.hasProperty(EnderEchoTunerBlock.FACING))
            return blockState.getValue(EnderEchoTunerBlock.FACING);

        return Direction.UP;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, EnderEchoTunerBlockEntity animatable, BakedGeoModel model, @Nullable RenderType renderType,
                               MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                               int packedOverlay, int colour) {
        if (animatable.getPos() != null && !animatable.getPos().equals(BlockPos.ZERO) && animatable.getName() != null) {
            poseStack.pushPose();
            // 渲染文本
            if (animatable.getBlockState().getValue(EnderEchoTunerBlock.FACING) != Direction.DOWN) poseStack.translate(0, 1.6f, 0);
            else poseStack.translate(0, -0.3f, 0);
            ResonatorNameRenderer.renderPositionName(animatable.getName(), bufferSource, poseStack);
            poseStack.popPose();
        }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
