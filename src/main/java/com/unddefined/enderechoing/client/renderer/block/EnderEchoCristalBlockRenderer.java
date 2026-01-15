package com.unddefined.enderechoing.client.renderer.block;

import com.unddefined.enderechoing.blocks.entity.EnderEchoCristalBlockEntity;
import com.unddefined.enderechoing.client.model.EnderEchoCristalBlockModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EnderEchoCristalBlockRenderer extends GeoBlockRenderer<EnderEchoCristalBlockEntity> {
    public EnderEchoCristalBlockRenderer() {
        super(new EnderEchoCristalBlockModel());
    }
}
