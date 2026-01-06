package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

import static com.unddefined.enderechoing.blocks.EnderEchoTunerBlock.CHARGED;

public class EnderEchoTunerModel extends DefaultedBlockGeoModel<EnderEchoTunerBlockEntity> {
    private final ResourceLocation R = ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_tuner");

    public EnderEchoTunerModel() {super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echo_tuner"));}

    @Override
    public ResourceLocation getModelResource(EnderEchoTunerBlockEntity animatable) {return buildFormattedModelPath(R);}

    @Override
    public ResourceLocation getTextureResource(EnderEchoTunerBlockEntity animatable) {
        return animatable.getBlockState().getValue(CHARGED) ? buildFormattedTexturePath(R.withSuffix("_charged"))
                : buildFormattedTexturePath(R);
    }

    @Override
    public ResourceLocation getAnimationResource(EnderEchoTunerBlockEntity animatable) {return buildFormattedAnimationPath(R);}

    @Override
    public RenderType getRenderType(EnderEchoTunerBlockEntity animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}
}
