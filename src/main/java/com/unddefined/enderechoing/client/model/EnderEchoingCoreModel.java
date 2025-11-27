package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.items.EnderEchoingCore;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class EnderEchoingCoreModel extends DefaultedItemGeoModel<EnderEchoingCore> {
    private final ResourceLocation modelPath = buildFormattedModelPath(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echoing_core"));
    private final ResourceLocation texturePath = buildFormattedTexturePath(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echoing_core"));
    private final ResourceLocation animationPath = buildFormattedAnimationPath(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echoing_core"));

    public EnderEchoingCoreModel() {super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echoing_core"));}

    @Override
    public ResourceLocation getModelResource(EnderEchoingCore animatable) {
        return modelPath;
    }

    @Override
    public ResourceLocation getTextureResource(EnderEchoingCore animatable) {
        return texturePath;
    }

    @Override
    public ResourceLocation getAnimationResource(EnderEchoingCore animatable) {
        return animationPath;
    }
    
    @Override
    public RenderType getRenderType(EnderEchoingCore animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}
}