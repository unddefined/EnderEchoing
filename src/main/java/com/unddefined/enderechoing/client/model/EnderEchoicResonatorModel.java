package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import com.unddefined.enderechoing.blocks.entity.EnderEchoicResonatorBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class EnderEchoicResonatorModel extends DefaultedBlockGeoModel<EnderEchoicResonatorBlockEntity> {
    private final ResourceLocation R = ResourceLocation.fromNamespaceAndPath( "enderechoing", "calibrated_sculk_shrieker");
    
    public EnderEchoicResonatorModel() {
        super(ResourceLocation.fromNamespaceAndPath("enderechoing", "ender_echoic_resonator"));
    }
    
    @Override
    public ResourceLocation getModelResource(EnderEchoicResonatorBlockEntity animatable) {
            return buildFormattedModelPath(R);
    }
    
    @Override
    public ResourceLocation getTextureResource(EnderEchoicResonatorBlockEntity animatable) {
        return buildFormattedTexturePath(R);
    }

    @Override
    public RenderType getRenderType(EnderEchoicResonatorBlockEntity animatable, ResourceLocation texture) {return RenderType.entityTranslucent(texture);}

}