package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.blocks.entity.CalibratedSculkShriekerBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class CalibratedSculkShriekerModel extends DefaultedBlockGeoModel<CalibratedSculkShriekerBlockEntity> {
    private final ResourceLocation modelPath = buildFormattedModelPath(ResourceLocation.fromNamespaceAndPath("enderechoing", "calibrated_sculk_shrieker"));
    private final ResourceLocation texturePath = buildFormattedTexturePath(ResourceLocation.fromNamespaceAndPath("enderechoing", "calibrated_sculk_shrieker"));
    private final ResourceLocation animationPath = buildFormattedAnimationPath(ResourceLocation.fromNamespaceAndPath("enderechoing", "calibrated_sculk_shrieker"));

    public CalibratedSculkShriekerModel() {
        super(ResourceLocation.fromNamespaceAndPath("enderechoing", "calibrated_sculk_shrieker"));
    }

    @Override
    public ResourceLocation getAnimationResource(CalibratedSculkShriekerBlockEntity animatable) {
        return animationPath;
    }

    @Override
    public ResourceLocation getModelResource(CalibratedSculkShriekerBlockEntity animatable) {
        return modelPath;
    }

    @Override
    public ResourceLocation getTextureResource(CalibratedSculkShriekerBlockEntity animatable) {
        return texturePath;
    }
}