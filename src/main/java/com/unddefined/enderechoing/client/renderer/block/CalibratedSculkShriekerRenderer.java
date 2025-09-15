package com.unddefined.enderechoing.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.unddefined.enderechoing.blocks.CalibratedSculkShriekerBlock;
import com.unddefined.enderechoing.blocks.entity.CalibratedSculkShriekerBlockEntity;
import com.unddefined.enderechoing.client.model.CalibratedSculkShriekerModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CalibratedSculkShriekerRenderer extends GeoBlockRenderer<CalibratedSculkShriekerBlockEntity> {
    public CalibratedSculkShriekerRenderer() {
        super(new CalibratedSculkShriekerModel());
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        switch (facing) {
            case SOUTH -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.translate(0, -0.5, -0.5);
            }
            case WEST -> {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                poseStack.translate(0.5, -0.5, 0);
            }
            case NORTH -> {
                poseStack.mulPose(Axis.XN.rotationDegrees(90));
                poseStack.translate(0, -0.5, 0.5);
            }
            case EAST -> {
                poseStack.mulPose(Axis.ZN.rotationDegrees(90));
                poseStack.translate(-0.5, -0.5, 0);
            }
            case DOWN -> {
                poseStack.mulPose(Axis.XN.rotationDegrees(180));
                poseStack.translate(0, -1.0, 0);
            }
            default -> poseStack.translate(0, 0, 0);

        }
    }

    @Override
    protected Direction getFacing(CalibratedSculkShriekerBlockEntity block) {
        BlockState blockState = block.getBlockState();

        // 检查我们自己的FACING属性
        if (blockState.hasProperty(CalibratedSculkShriekerBlock.FACING)) return blockState.getValue(CalibratedSculkShriekerBlock.FACING);

        return Direction.UP;
    }
}