package com.unddefined.enderechoing.server.registry;

import com.unddefined.enderechoing.blocks.entity.CalibratedSculkShriekerBlockEntity;
import com.unddefined.enderechoing.blocks.entity.EchoDruseBlockEntity;
import com.unddefined.enderechoing.blocks.entity.EnderEchoicTeleporterBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "enderechoing");

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderEchoicTeleporterBlockEntity>> ENDER_ECHOIC_TELEPORTER =
            BLOCK_ENTITY_TYPES.register("ender_echoic_teleporter_blockentity", () -> BlockEntityType.Builder.<EnderEchoicTeleporterBlockEntity>of(
                    EnderEchoicTeleporterBlockEntity::new,
                    BlockRegistry.ENDER_ECHOIC_TELEPORTER.get()
            ).build(null));
            
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CalibratedSculkShriekerBlockEntity>> CALIBRATED_SCULK_SHRIEKER =
            BLOCK_ENTITY_TYPES.register("calibrated_sculk_shrieker_blockentity", () -> BlockEntityType.Builder.<CalibratedSculkShriekerBlockEntity>of(
                    CalibratedSculkShriekerBlockEntity::new,
                    BlockRegistry.CALIBRATED_SCULK_SHRIEKER.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EchoDruseBlockEntity>> ECHO_DRUSE =
            BLOCK_ENTITY_TYPES.register("echo_druse_blockentity", () -> BlockEntityType.Builder.<EchoDruseBlockEntity>of(
                    EchoDruseBlockEntity::new,
                    BlockRegistry.ECHO_DRUSE.get()
            ).build(null));
}