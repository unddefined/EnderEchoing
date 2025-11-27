package com.unddefined.enderechoing.client.renderer.item;

import com.unddefined.enderechoing.client.model.EnderEchoResonateBoxModel;
import com.unddefined.enderechoing.items.EnderEchoResonateBox;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class EnderEchoResonateBoxRenderer extends GeoItemRenderer<EnderEchoResonateBox> {
    public EnderEchoResonateBoxRenderer() {
        super(new EnderEchoResonateBoxModel());
//        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
