package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.items.EnderEchoCrystal;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class EnderEchoCrystalModel extends DefaultedItemGeoModel<EnderEchoCrystal> {
    private final ResourceLocation R = ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_crystal");
    public EnderEchoCrystalModel() {super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_crystal"));}
    @Override
    public ResourceLocation getModelResource(EnderEchoCrystal animatable) {return buildFormattedModelPath(R);}

    @Override
    public ResourceLocation getTextureResource(EnderEchoCrystal animatable) {return buildFormattedTexturePath(R);}

    @Override
    public ResourceLocation getAnimationResource(EnderEchoCrystal animatable) {return buildFormattedAnimationPath(R);}

    @Override
    public RenderType getRenderType(EnderEchoCrystal animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}
}
