package com.unddefined.enderechoing.registry;

import com.unddefined.enderechoing.blocks.entity.CalibratedSculkShrienkerBlockEntity;
import com.unddefined.enderechoing.blocks.entity.EnderEchoicTeleporterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "enderechoing");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderEchoicTeleporterBlockEntity>> ENDER_ECHOIC_TELEPORTER =
            BLOCK_ENTITY_TYPES.register("ender_echoic_teleporter", () -> BlockEntityType.Builder.<EnderEchoicTeleporterBlockEntity>of(
                    EnderEchoicTeleporterBlockEntity::new,
                    BlockRegistry.ENDER_ECHOIC_TELEPORTER.get()
            ).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CalibratedSculkShrienkerBlockEntity>> CALIBRATED_SCULK_SHRIENKER =
            BLOCK_ENTITY_TYPES.register("calibrated_sculk_shrienker", () -> BlockEntityType.Builder.<CalibratedSculkShrienkerBlockEntity>of(
                    CalibratedSculkShrienkerBlockEntity::new,
                    BlockRegistry.CALIBRATED_SCULK_SHRIENKER.get()
            ).build(null));
}