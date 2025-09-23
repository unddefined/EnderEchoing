package com.unddefined.enderechoing.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.unddefined.enderechoing.blocks.entity.CalibratedSculkShriekerBlockEntity;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import static com.unddefined.enderechoing.blocks.CalibratedSculkShriekerBlock.FACING;

public class CalibratedSculkShriekerLayer extends BlockAndItemGeoLayer<CalibratedSculkShriekerBlockEntity> {
    public CalibratedSculkShriekerLayer(GeoRenderer<CalibratedSculkShriekerBlockEntity> renderer) {
        super(renderer);
    }

    @Override
    protected ItemStack getStackForBone(GeoBone bone, CalibratedSculkShriekerBlockEntity animatable) {
        // 只在特定骨骼上渲染物品
        if (bone.getName().equals("item")) return animatable.getItemHandler().getStackInSlot(0);

        return null;
    }

    @Override
    protected BlockState getBlockForBone(GeoBone bone, CalibratedSculkShriekerBlockEntity animatable) {
        return null;
    }

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, CalibratedSculkShriekerBlockEntity animatable,
                                      MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        poseStack.scale(0.5f, 0.5f, 0.5f);
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        // 距离小于1时不渲染物品
        if (Minecraft.getInstance().cameraEntity != null) {
            double distance = camera.getBlockPosition().distToCenterSqr(animatable.getBlockPos().getCenter());
            if (distance < 1.0) return;
        }

        // 使物品始终面向玩家
        if (stack.is(ItemRegistry.ENDER_ECHOING_PEARL) || stack.is(Items.ENDER_EYE)) {
            if (Minecraft.getInstance().level != null && Minecraft.getInstance().cameraEntity != null) {
                Direction facing = animatable.getBlockState().getValue(FACING);
                // 首先抵消方块的旋转
                switch (facing) {
                    case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90));

                    case WEST -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

                    case NORTH -> poseStack.mulPose(Axis.XN.rotationDegrees(-90));

                    case EAST -> poseStack.mulPose(Axis.ZN.rotationDegrees(-90));

                    case DOWN -> poseStack.mulPose(Axis.XN.rotationDegrees(-180));

                    default -> poseStack.mulPose(Axis.XN.rotationDegrees(0));
                }

                // 再调整朝向玩家
                poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
                poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
            }
        }

        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
    }
}