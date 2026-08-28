package com.unddefined.enderechoing.client.renderer.item;

import com.unddefined.enderechoing.client.model.WarpCoreModel;
import com.unddefined.enderechoing.items.WarpCore;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WarpCoreRenderer extends GeoItemRenderer<WarpCore> {
    public  WarpCoreRenderer(GeoModel<WarpCore> model) {
        super(new WarpCoreModel());
    }
}
