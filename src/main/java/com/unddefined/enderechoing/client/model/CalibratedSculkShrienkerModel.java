package com.unddefined.enderechoing.client.model;

import com.unddefined.enderechoing.blocks.entity.CalibratedSculkShrienkerBlockEntity;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class CalibratedSculkShrienkerModel extends DefaultedBlockGeoModel<CalibratedSculkShrienkerBlockEntity> {
    public CalibratedSculkShrienkerModel() {
        super(ResourceLocation.fromNamespaceAndPath("enderechoing", "calibrated_sculk_shrienker"));
    }
}
