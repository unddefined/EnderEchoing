package com.unddefined.enderechoing.server.DataComponents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnderEchoingPearlData {
    public record PositionData(int x, int y, int z, String dimension){
        public static final Codec<PositionData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("x").forGetter(PositionData::x),
                        Codec.INT.fieldOf("y").forGetter(PositionData::y),
                        Codec.INT.fieldOf("z").forGetter(PositionData::z),
                        Codec.STRING.fieldOf("dimension").forGetter(PositionData::dimension)
                ).apply(instance, PositionData::new)
        );
    };
    
    public record EntityData(){
        public static final Codec<EntityData> CODEC = Codec.unit(EntityData::new);
    };

    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "enderechoing");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PositionData>> POSITION = REGISTRAR.registerComponentType("position", builder -> builder.persistent(PositionData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EntityData>> ENTITY = REGISTRAR.registerComponentType("entity", builder -> builder.persistent(EntityData.CODEC));

}