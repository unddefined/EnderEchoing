package com.unddefined.enderechoing.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.unddefined.enderechoing.client.model.EnderEchoCrystalEntityModel;
import com.unddefined.enderechoing.entities.EnderEchoCrystalEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EnderEchoCrystalEntityRenderer extends GeoEntityRenderer<EnderEchoCrystalEntity> {
    public EnderEchoCrystalEntityRenderer(EntityRendererProvider.Context c) {
        super(c, new EnderEchoCrystalEntityModel());
    }
}
