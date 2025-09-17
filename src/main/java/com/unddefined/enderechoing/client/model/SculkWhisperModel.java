package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.blocks.entity.SculkWhisperBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class SculkWhisperModel extends DefaultedBlockGeoModel<SculkWhisperBlockEntity> {
    private final ResourceLocation modelPath = buildFormattedModelPath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "sculk_whisper"));
    private final ResourceLocation texturePath = buildFormattedTexturePath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "sculk_whisper"));
    private final ResourceLocation animationPath = buildFormattedAnimationPath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "sculk_whisper"));
    public SculkWhisperModel() {
        super(ResourceLocation.fromNamespaceAndPath("enderechoing", "sculk_whisper"));
    }
    @Override
    public ResourceLocation getAnimationResource( SculkWhisperBlockEntity animatable){
        return animationPath;
    }
    @Override
    public ResourceLocation getModelResource(SculkWhisperBlockEntity animatable) {
        return modelPath;
    }
    @Override
    public ResourceLocation getTextureResource(SculkWhisperBlockEntity animatable) {return texturePath;}
}
