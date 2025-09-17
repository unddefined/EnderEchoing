package com.unddefined.enderechoing.server.registry;

import com.unddefined.enderechoing.blocks.CalibratedSculkShriekerBlock;
import com.unddefined.enderechoing.blocks.EchoDruseBlock;
import com.unddefined.enderechoing.blocks.EnderEchoicTeleporterBlock;
import com.unddefined.enderechoing.blocks.SculkWhisperBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("enderechoing");

    public static final DeferredBlock<EnderEchoicTeleporterBlock> ENDER_ECHOIC_TELEPORTER = BLOCKS.register("ender_echoic_teleporter", EnderEchoicTeleporterBlock::new);
    public static final DeferredBlock<CalibratedSculkShriekerBlock> CALIBRATED_SCULK_SHRIEKER = BLOCKS.register("calibrated_sculk_shrieker",CalibratedSculkShriekerBlock::new);
    public static final DeferredBlock<EchoDruseBlock> ECHO_DRUSE = BLOCKS.register("echo_druse_block", EchoDruseBlock::new);
    public static final DeferredBlock<SculkWhisperBlock> SCULK_WHISPER = BLOCKS.register("sculk_whisper", SculkWhisperBlock::new);
}