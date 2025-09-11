package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.blocks.entity.EnderEchoicTeleporterBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class EnderEchoicTeleporterModel extends DefaultedBlockGeoModel<EnderEchoicTeleporterBlockEntity> {
    private final ResourceLocation modelPath = buildFormattedModelPath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "ender_echoic_teleporter"));
    private final ResourceLocation texturePath = buildFormattedTexturePath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "calibrated_sculk_shrienker"));
    private final ResourceLocation animationPath = buildFormattedAnimationPath(ResourceLocation.fromNamespaceAndPath( "enderechoing", "ender_echoic_teleporter"));
    public EnderEchoicTeleporterModel() {
        super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echoic_teleporter"));
    }
    @Override
    public ResourceLocation getAnimationResource( EnderEchoicTeleporterBlockEntity animatable){
            return animationPath;
    }
    @Override
    public ResourceLocation getModelResource(EnderEchoicTeleporterBlockEntity animatable) {
            return modelPath;
    }
    @Override
    public ResourceLocation getTextureResource(EnderEchoicTeleporterBlockEntity animatable) {return texturePath;}
}
