package com.unddefined.enderechoing.client.renderer.block;

import com.unddefined.enderechoing.blocks.entity.EnderEchoicTeleporterBlockEntity;
import com.unddefined.enderechoing.client.model.EnderEchoicTeleporterModel;
import com.unddefined.enderechoing.client.renderer.layer.EnderEchoicTeleporterLayer;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EnderEchoicTeleporterRenderer extends GeoBlockRenderer<EnderEchoicTeleporterBlockEntity> {
    public EnderEchoicTeleporterRenderer() {
        super(new EnderEchoicTeleporterModel());
        // 添加渲染层以在方块上渲染EnderEchoingCore物品
        this.addRenderLayer(new EnderEchoicTeleporterLayer(this));
    }
}