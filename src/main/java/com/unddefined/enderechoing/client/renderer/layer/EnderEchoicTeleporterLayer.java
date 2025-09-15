package com.unddefined.enderechoing.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unddefined.enderechoing.blocks.entity.EnderEchoicTeleporterBlockEntity;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class EnderEchoicTeleporterLayer extends BlockAndItemGeoLayer<EnderEchoicTeleporterBlockEntity> {

    public EnderEchoicTeleporterLayer(GeoRenderer<EnderEchoicTeleporterBlockEntity> renderer) {
        super(renderer, 
              (bone, animatable) -> {
                  // 只在特定骨骼上渲染物品
                  if (bone.getName().equals("EnderEchoingCore")) {
                      return new ItemStack(ItemRegistry.ENDER_ECHOING_CORE.get());
                  }
                  return null;
              }, 
              (bone, animatable) -> null);
    }

    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, EnderEchoicTeleporterBlockEntity animatable,
                                      MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        // 修正位置
        poseStack.translate(0, -0.18, 0);
        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
    }
}