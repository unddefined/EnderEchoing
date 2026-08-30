package com.unddefined.enderechoing.client.model.item;

import com.unddefined.enderechoing.items.WarpCore;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class WarpCoreModel extends DefaultedItemGeoModel<WarpCore> {
    private final ResourceLocation R = ResourceLocation.fromNamespaceAndPath("enderechoing", "warp_core");
    public WarpCoreModel() {super(ResourceLocation.fromNamespaceAndPath("enderechoing", "warp_core"));}
    @Override
    public ResourceLocation getModelResource(WarpCore animatable) {return buildFormattedModelPath(R);}

    @Override
    public ResourceLocation getTextureResource(WarpCore animatable) {return buildFormattedTexturePath(R);}

    @Override
    public ResourceLocation getAnimationResource(WarpCore animatable) {return buildFormattedAnimationPath(R);}

    @Override
    public RenderType getRenderType(WarpCore animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}
}
