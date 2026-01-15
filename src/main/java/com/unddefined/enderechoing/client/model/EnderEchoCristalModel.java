package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.items.EnderEchoCristal;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class EnderEchoCristalModel extends DefaultedItemGeoModel<EnderEchoCristal> {
    private final ResourceLocation R = ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_cristal");
    public EnderEchoCristalModel() {super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_cristal"));}
    @Override
    public ResourceLocation getModelResource(EnderEchoCristal animatable) {return buildFormattedModelPath(R);}

    @Override
    public ResourceLocation getTextureResource(EnderEchoCristal animatable) {return buildFormattedTexturePath(R);}

    @Override
    public ResourceLocation getAnimationResource(EnderEchoCristal animatable) {return buildFormattedAnimationPath(R);}

    @Override
    public RenderType getRenderType(EnderEchoCristal animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}
}
