package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.blocks.entity.EnderEchoCristalBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class EnderEchoCristalBlockModel extends DefaultedBlockGeoModel<EnderEchoCristalBlockEntity> {
    private final ResourceLocation R = ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_cristal");
    public EnderEchoCristalBlockModel() {super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_cristal"));}
    @Override
    public ResourceLocation getModelResource(EnderEchoCristalBlockEntity animatable) {return buildFormattedModelPath(R);}

    @Override
    public ResourceLocation getTextureResource(EnderEchoCristalBlockEntity animatable) {return buildFormattedTexturePath(R);}

    @Override
    public ResourceLocation getAnimationResource(EnderEchoCristalBlockEntity animatable) {return buildFormattedAnimationPath(R);}

    @Override
    public RenderType getRenderType(EnderEchoCristalBlockEntity animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}
}
