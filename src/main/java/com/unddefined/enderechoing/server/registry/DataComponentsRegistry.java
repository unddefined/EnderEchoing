package com.unddefined.enderechoing.server.registry;

import com.unddefined.enderechoing.server.DataComponents.EntityData;
import com.unddefined.enderechoing.server.DataComponents.PositionData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DataComponentsRegistry {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "enderechoing");
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PositionData>> POSITION = REGISTRAR.registerComponentType("position", builder -> builder.persistent(PositionData.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EntityData>> ENTITY = REGISTRAR.registerComponentType("entity", builder -> builder.persistent(EntityData.CODEC));

}
