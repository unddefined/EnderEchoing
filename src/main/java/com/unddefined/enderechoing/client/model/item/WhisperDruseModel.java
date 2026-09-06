package com.unddefined.enderechoing.client.model.item;

import com.unddefined.enderechoing.items.WhisperDruse;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class WhisperDruseModel extends DefaultedItemGeoModel<WhisperDruse> {
    private final ResourceLocation R = ResourceLocation.fromNamespaceAndPath("enderechoing", "whisper_druse");
    public WhisperDruseModel() {
        super(ResourceLocation.fromNamespaceAndPath("enderechoing", "whisper_druse"));
    }

    @Override
    public ResourceLocation getModelResource(WhisperDruse animatable) {return buildFormattedModelPath(R);}

    @Override
    public ResourceLocation getTextureResource(WhisperDruse animatable) {return buildFormattedTexturePath(R);}

    @Override
    public ResourceLocation getAnimationResource(WhisperDruse animatable) {return buildFormattedAnimationPath(R);}

    @Override
    public RenderType getRenderType(WhisperDruse animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}
}
