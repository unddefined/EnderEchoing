package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.blocks.entity.EnderEchoCrystalBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class EnderEchoCrystalBlockModel extends DefaultedBlockGeoModel<EnderEchoCrystalBlockEntity> {
    private final ResourceLocation R = ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_crystal");
    public EnderEchoCrystalBlockModel() {super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_crystal"));}
    @Override
    public ResourceLocation getModelResource(EnderEchoCrystalBlockEntity animatable) {return buildFormattedModelPath(R);}

    @Override
    public ResourceLocation getTextureResource(EnderEchoCrystalBlockEntity animatable) {return buildFormattedTexturePath(R);}

    @Override
    public ResourceLocation getAnimationResource(EnderEchoCrystalBlockEntity animatable) {return buildFormattedAnimationPath(R);}

    @Override
    public RenderType getRenderType(EnderEchoCrystalBlockEntity animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}
}
