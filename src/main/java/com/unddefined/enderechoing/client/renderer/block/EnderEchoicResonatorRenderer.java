package com.unddefined.enderechoing.client.renderer.block;

import com.unddefined.enderechoing.blocks.entity.EnderEchoicResonatorBlockEntity;
import com.unddefined.enderechoing.client.model.EnderEchoicResonatorModel;
import com.unddefined.enderechoing.client.renderer.layer.EnderEchoicResonatorLayer;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EnderEchoicResonatorRenderer extends GeoBlockRenderer<EnderEchoicResonatorBlockEntity> {
    public EnderEchoicResonatorRenderer() {
        super(new EnderEchoicResonatorModel());
        // 添加渲染层以在方块上渲染EnderEchoingCore物品
        this.addRenderLayer(new EnderEchoicResonatorLayer(this));
    }
}