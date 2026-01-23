package com.unddefined.enderechoing.server.registry;

import com.mojang.serialization.Codec;
import com.unddefined.enderechoing.server.DataComponents.EntityData;
import com.unddefined.enderechoing.util.IconListManager;
import com.unddefined.enderechoing.util.MarkedPositionsManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class DataRegistry {
    public static final DeferredRegister.DataComponents COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, "enderechoing");
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, "enderechoing");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> POSITION = COMPONENT_TYPES.registerComponentType("position", builder -> builder.persistent(GlobalPos.CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<EntityData>> ENTITY = COMPONENT_TYPES.registerComponentType("entity", builder -> builder.persistent(EntityData.CODEC));
    public static final Supplier<AttachmentType<MarkedPositionsManager>> MARKED_POSITIONS_CACHE = ATTACHMENT_TYPES.register(
            "marked_positions_cache", () -> AttachmentType.serializable(MarkedPositionsManager::new).copyOnDeath().build()
    );
    public static final Supplier<AttachmentType<Integer>> EE_PEARL_AMOUNT = ATTACHMENT_TYPES.register(
            "ee_pearl_amount", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build()
    );
    public static final Supplier<AttachmentType<Integer>> SELECTED_TUNER_TAB = ATTACHMENT_TYPES.register(
             "selected_tuner_tab", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build()
    );
    public static final Supplier<AttachmentType<IconListManager>> ICON_LIST = ATTACHMENT_TYPES.register(
            "icon_list", () -> AttachmentType.serializable(IconListManager::new).copyOnDeath().build()
    );
    public static final Supplier<AttachmentType<BlockPos>> EE_PEARL_POSITION = ATTACHMENT_TYPES.register(
            "ee_pearl_position", () -> AttachmentType.builder(() -> BlockPos.ZERO).build()
    );
}