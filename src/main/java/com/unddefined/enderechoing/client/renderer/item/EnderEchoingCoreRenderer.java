package com.unddefined.enderechoing.client.renderer.item;

import com.unddefined.enderechoing.client.model.EnderEchoingCoreModel;
import com.unddefined.enderechoing.items.EnderEchoingCore;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class EnderEchoingCoreRenderer extends GeoItemRenderer<EnderEchoingCore> {
    public EnderEchoingCoreRenderer(GeoModel<EnderEchoingCore> model) {
        super(new EnderEchoingCoreModel());
    }
}
