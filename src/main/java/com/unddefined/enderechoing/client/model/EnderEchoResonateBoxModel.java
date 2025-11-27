package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.items.EnderEchoResonateBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class EnderEchoResonateBoxModel extends DefaultedItemGeoModel<EnderEchoResonateBox> {
    private final ResourceLocation R = ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_resonate_box");
    public EnderEchoResonateBoxModel() {super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_resonate_box"));}
    @Override
    public ResourceLocation getModelResource(EnderEchoResonateBox animatable) {return buildFormattedModelPath(R);}

    @Override
    public ResourceLocation getTextureResource(EnderEchoResonateBox animatable) {return buildFormattedTexturePath(R);}

    @Override
    public ResourceLocation getAnimationResource(EnderEchoResonateBox animatable) {return buildFormattedAnimationPath(R);}

    @Override
    public RenderType getRenderType(EnderEchoResonateBox animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}
}
