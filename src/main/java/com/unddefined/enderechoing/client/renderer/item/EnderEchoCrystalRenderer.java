package com.unddefined.enderechoing.client.renderer.item;

import com.unddefined.enderechoing.client.model.EnderEchoCrystalModel;
import com.unddefined.enderechoing.items.EnderEchoCrystal;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class EnderEchoCrystalRenderer extends GeoItemRenderer<EnderEchoCrystal> {
    public EnderEchoCrystalRenderer() {
        super(new EnderEchoCrystalModel());
//        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
