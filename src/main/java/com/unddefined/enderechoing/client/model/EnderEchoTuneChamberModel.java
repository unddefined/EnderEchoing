package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.items.EnderEchoTuneChamber;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class EnderEchoTuneChamberModel extends DefaultedItemGeoModel<EnderEchoTuneChamber> {
    private final ResourceLocation R = ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_tune_chamber");
    public EnderEchoTuneChamberModel() {super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_tune_chamber"));}
    @Override
    public ResourceLocation getModelResource(EnderEchoTuneChamber animatable) {return buildFormattedModelPath(R);}

    @Override
    public ResourceLocation getTextureResource(EnderEchoTuneChamber animatable) {return buildFormattedTexturePath(R);}

    @Override
    public ResourceLocation getAnimationResource(EnderEchoTuneChamber animatable) {return buildFormattedAnimationPath(R);}

    @Override
    public RenderType getRenderType(EnderEchoTuneChamber animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}
}
