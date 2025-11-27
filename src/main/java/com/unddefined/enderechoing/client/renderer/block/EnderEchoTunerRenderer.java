package com.unddefined.enderechoing.client.renderer.block;

import com.unddefined.enderechoing.blocks.entity.EnderEchoTunerBlockEntity;
import com.unddefined.enderechoing.client.model.EnderEchoTunerModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EnderEchoTunerRenderer extends GeoBlockRenderer<EnderEchoTunerBlockEntity> {
    public EnderEchoTunerRenderer() {
        super(new EnderEchoTunerModel());
    }
}
