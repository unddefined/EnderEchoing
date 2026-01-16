package com.unddefined.enderechoing.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class EnderEchoTunerLayer extends BlockAndItemGeoLayer<EnderEchoTunerBlockEntity> {

    public EnderEchoTunerLayer(GeoRenderer<EnderEchoTunerBlockEntity> renderer) {
        super(renderer);
    }

    @Override
    protected ItemStack getStackForBone(GeoBone bone, EnderEchoTunerBlockEntity animatable) {
        // 只在特定骨骼上渲染物品
        if (bone.getName().equals("box")) return new ItemStack(ItemRegistry.ENDER_ECHO_TUNE_CHAMBER.get());
        return this.stackForBone.apply(bone, animatable);
    }

    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, EnderEchoTunerBlockEntity animatable,
                                      MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        // 距离小于1时不渲染chamber
        if (Minecraft.getInstance().cameraEntity != null && camera.getBlockPosition().distToCenterSqr(animatable.getBlockPos().getCenter()) < 1.0)
            return;

        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
    }
}