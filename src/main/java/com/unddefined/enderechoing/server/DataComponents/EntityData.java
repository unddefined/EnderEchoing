package com.unddefined.enderechoing.server.DataComponents;

import com.mojang.serialization.Codec;


public record EntityData() {
    public static final Codec<EntityData> CODEC = Codec.unit(EntityData::new);
}

