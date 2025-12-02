package com.unddefined.enderechoing.server.DataComponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record PositionData(ResourceKey<Level> Dimension, BlockPos pos){
    public static final Codec<PositionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(PositionData::Dimension),
                    BlockPos.CODEC.fieldOf("pos").forGetter(PositionData::pos)
            ).apply(instance, PositionData::new)
    );
}