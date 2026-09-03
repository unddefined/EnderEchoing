package com.unddefined.enderechoing.server.DataComponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;


public record EntityData(UUID playerId) {
    public static final Codec<EntityData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.fieldOf("player_id").forGetter(EntityData::playerId)
            ).apply(instance, EntityData::new));
}
