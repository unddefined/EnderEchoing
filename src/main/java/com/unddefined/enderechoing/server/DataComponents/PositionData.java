package com.unddefined.enderechoing.server.DataComponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PositionData(int x, int y, int z, String dimension){
    public static final Codec<PositionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("x").forGetter(PositionData::x),
                    Codec.INT.fieldOf("y").forGetter(PositionData::y),
                    Codec.INT.fieldOf("z").forGetter(PositionData::z),
                    Codec.STRING.fieldOf("dimension").forGetter(PositionData::dimension)
            ).apply(instance, PositionData::new)
    );
}