package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.blocks.entity.EnderEchoicTeleporterBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class EnderEchoicTeleporterModel extends DefaultedBlockGeoModel<EnderEchoicTeleporterBlockEntity> {
    private final ResourceLocation modelPath = buildFormattedModelPath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "ender_echoic_teleporter"));
    private final ResourceLocation texturePath = buildFormattedTexturePath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "ender_echoic_teleporter"));
    private final ResourceLocation animationPath = buildFormattedAnimationPath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "ender_echoic_teleporter"));
    public EnderEchoicTeleporterModel() {
        super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echoic_teleporter"));
    }
    @Override
    public ResourceLocation getAnimationResource( EnderEchoicTeleporterBlockEntity animatable){
        if (animatable.getLevel().isRaining()) {
            return super.getAnimationResource(animatable);
        }
        else {
            return animationPath;
        }
    }
    @Override
    public ResourceLocation getModelResource(EnderEchoicTeleporterBlockEntity animatable) {
        if (animatable.getLevel().isRaining()) {
            return super.getModelResource(animatable);
        }
        else {
            return modelPath;
        }
    }
    @Override
    public ResourceLocation getTextureResource(EnderEchoicTeleporterBlockEntity animatable) {
        if (animatable.getLevel().isRaining()) {
            return super.getTextureResource(animatable);
        }
        else {
            return texturePath;
        }
    }
    @Override
    public RenderType getRenderType(EnderEchoicTeleporterBlockEntity animatable, ResourceLocation texture){
        return RenderType.entityTranslucent(texture);
    }
}
