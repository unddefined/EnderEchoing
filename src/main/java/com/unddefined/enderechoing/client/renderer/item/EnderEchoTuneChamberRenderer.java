package com.unddefined.enderechoing.client.renderer.item;

import com.unddefined.enderechoing.client.model.EnderEchoTuneChamberModel;
import com.unddefined.enderechoing.items.EnderEchoTuneChamber;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class EnderEchoTuneChamberRenderer extends GeoItemRenderer<EnderEchoTuneChamber> {
    public EnderEchoTuneChamberRenderer() {
        super(new EnderEchoTuneChamberModel());
//        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
