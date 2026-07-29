package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.entities.EnderEchoCrystalEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class EnderEchoCrystalEntityModel extends DefaultedEntityGeoModel<EnderEchoCrystalEntity> {
    private final ResourceLocation E = ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_crystal");

    public EnderEchoCrystalEntityModel() {
        super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_crystal"));
    }
    @Override
    public ResourceLocation getModelResource(EnderEchoCrystalEntity animatable) {return buildFormattedModelPath(E);}

    @Override
    public ResourceLocation getTextureResource(EnderEchoCrystalEntity animatable) {return buildFormattedTexturePath(E);}

    @Override
    public ResourceLocation getAnimationResource(EnderEchoCrystalEntity animatable) {return buildFormattedAnimationPath(E);}

    @Override
    public RenderType getRenderType(EnderEchoCrystalEntity animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}

}
