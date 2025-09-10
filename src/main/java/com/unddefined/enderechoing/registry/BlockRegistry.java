package com.unddefined.enderechoing.registry;

import com.unddefined.enderechoing.blocks.CalibratedSculkShrienkerBlock;
import com.unddefined.enderechoing.blocks.EnderEchoicTeleporterBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("enderechoing");

    public static final DeferredBlock<EnderEchoicTeleporterBlock> ENDER_ECHOIC_TELEPORTER = BLOCKS.register("ender_echoic_teleporter", EnderEchoicTeleporterBlock::new);
    public static final DeferredBlock<CalibratedSculkShrienkerBlock> CALIBRATED_SCULK_SHRIENKER = BLOCKS.register("calibrated_sculk_shrienker",CalibratedSculkShrienkerBlock::new);
}