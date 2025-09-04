package com.unddefined.enderechoing.client.renderer.block;

import com.unddefined.enderechoing.blocks.entity.EnderEchoicTeleporterBlockEntity;
import com.unddefined.enderechoing.client.model.EnderEchoicTeleporterModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EnderEchoicTeleporterRenderer extends GeoBlockRenderer<EnderEchoicTeleporterBlockEntity> {
    public EnderEchoicTeleporterRenderer() {
        super(new EnderEchoicTeleporterModel());
    }
}