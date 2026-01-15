package com.unddefined.enderechoing.client.renderer.item;

import com.unddefined.enderechoing.client.model.EnderEchoCristalModel;
import com.unddefined.enderechoing.items.EnderEchoCristal;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class EnderEchoCristalRenderer extends GeoItemRenderer<EnderEchoCristal> {
    public EnderEchoCristalRenderer() {
        super(new EnderEchoCristalModel());
//        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
