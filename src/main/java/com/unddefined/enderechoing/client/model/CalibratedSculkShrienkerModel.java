package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.blocks.entity.CalibratedSculkShrienkerBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class CalibratedSculkShrienkerModel extends DefaultedBlockGeoModel<CalibratedSculkShrienkerBlockEntity> {
    private final ResourceLocation modelPath = buildFormattedModelPath(ResourceLocation.fromNamespaceAndPath("enderechoing", "calibrated_sculk_shrienker"));
    private final ResourceLocation texturePath = buildFormattedTexturePath(ResourceLocation.fromNamespaceAndPath("enderechoing", "calibrated_sculk_shrienker"));
    private final ResourceLocation animationPath = buildFormattedAnimationPath(ResourceLocation.fromNamespaceAndPath("enderechoing", "calibrated_sculk_shrienker"));

    public CalibratedSculkShrienkerModel() {
        super(ResourceLocation.fromNamespaceAndPath("enderechoing", "calibrated_sculk_shrienker"));
    }

    @Override
    public ResourceLocation getAnimationResource(CalibratedSculkShrienkerBlockEntity animatable) {
        return animationPath;
    }

    @Override
    public ResourceLocation getModelResource(CalibratedSculkShrienkerBlockEntity animatable) {
        return modelPath;
    }

    @Override
    public ResourceLocation getTextureResource(CalibratedSculkShrienkerBlockEntity animatable) {
        return texturePath;
    }
}