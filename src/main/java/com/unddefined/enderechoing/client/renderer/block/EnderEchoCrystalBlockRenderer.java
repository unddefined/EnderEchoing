package com.unddefined.enderechoing.client.renderer.block;

import com.unddefined.enderechoing.blocks.entity.EnderEchoCrystalBlockEntity;
import com.unddefined.enderechoing.client.model.EnderEchoCrystalBlockModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EnderEchoCrystalBlockRenderer extends GeoBlockRenderer<EnderEchoCrystalBlockEntity> {
    public EnderEchoCrystalBlockRenderer() {super(new EnderEchoCrystalBlockModel());}
}
