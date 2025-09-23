package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.blocks.entity.EnderEchoicResonatorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class EnderEchoicResonatorModel extends DefaultedBlockGeoModel<EnderEchoicResonatorBlockEntity> {
    private final ResourceLocation modelPath = buildFormattedModelPath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "ender_echoic_resonator"));
    private final ResourceLocation texturePath = buildFormattedTexturePath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "calibrated_sculk_shrieker"));
    private final ResourceLocation animationPath = buildFormattedAnimationPath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "ender_echoic_resonator"));
    public EnderEchoicResonatorModel() {
        super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echoic_resonator"));
    }
    @Override
    public ResourceLocation getAnimationResource( EnderEchoicResonatorBlockEntity animatable){
            return animationPath;
    }
    @Override
    public ResourceLocation getModelResource(EnderEchoicResonatorBlockEntity animatable) {
            return modelPath;
    }
    @Override
    public ResourceLocation getTextureResource(EnderEchoicResonatorBlockEntity animatable) {return texturePath;}
}
