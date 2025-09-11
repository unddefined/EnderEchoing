package com.unddefined.enderechoing.client.renderer.block;

import com.unddefined.enderechoing.blocks.entity.CalibratedSculkShrienkerBlockEntity;
import com.unddefined.enderechoing.client.model.CalibratedSculkShrienkerModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CalibratedSculkShrienkerRenderer extends GeoBlockRenderer<CalibratedSculkShrienkerBlockEntity> {
    public CalibratedSculkShrienkerRenderer() {
        super(new CalibratedSculkShrienkerModel());
    }
}