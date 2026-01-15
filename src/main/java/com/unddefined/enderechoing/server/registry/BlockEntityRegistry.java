package com.unddefined.enderechoing.server.registry;

import com.unddefined.enderechoing.blocks.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "enderechoing");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderEchoicResonatorBlockEntity>> ENDER_ECHOIC_RESONATOR =
            BLOCK_ENTITY_TYPES.register("ender_echoic_resonator_blockentity", () -> BlockEntityType.Builder.of(
                    EnderEchoicResonatorBlockEntity::new,
                    BlockRegistry.ENDER_ECHOIC_RESONATOR.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderEchoTunerBlockEntity>> ENDER_ECHO_TUNER =
            BLOCK_ENTITY_TYPES.register("ender_echo_tuner_blockentity", () -> BlockEntityType.Builder.of(
                    EnderEchoTunerBlockEntity::new,
                    BlockRegistry.ENDER_ECHO_TUNER.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderEchoCrystalBlockEntity>> ENDER_ECHO_CRYSTAL =
            BLOCK_ENTITY_TYPES.register("ender_echo_crystal_blockentity", () -> BlockEntityType.Builder.of(
                    EnderEchoCrystalBlockEntity::new,
                    BlockRegistry.ENDER_ECHO_CRYSTAL.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CalibratedSculkShriekerBlockEntity>> CALIBRATED_SCULK_SHRIEKER =
            BLOCK_ENTITY_TYPES.register("calibrated_sculk_shrieker_blockentity", () -> BlockEntityType.Builder.of(
                    CalibratedSculkShriekerBlockEntity::new,
                    BlockRegistry.CALIBRATED_SCULK_SHRIEKER.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EchoDruseBlockEntity>> ECHO_DRUSE =
            BLOCK_ENTITY_TYPES.register("echo_druse_blockentity", () -> BlockEntityType.Builder.of(
                    EchoDruseBlockEntity::new,
                    BlockRegistry.ECHO_DRUSE.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SculkWhisperBlockEntity>> SCULK_WHISPER =
            BLOCK_ENTITY_TYPES.register("sculk_whisper", () -> BlockEntityType.Builder.of(
                    SculkWhisperBlockEntity::new,
                    BlockRegistry.SCULK_WHISPER.get()
            ).build(null));
}