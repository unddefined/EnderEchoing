package com.unddefined.enderechoing.server.registry;

import com.unddefined.enderechoing.blocks.*;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("enderechoing");

    public static final DeferredBlock<EnderEchoicResonatorBlock> ENDER_ECHOIC_RESONATOR = BLOCKS.register("ender_echoic_resonator", EnderEchoicResonatorBlock::new);
    public static final DeferredBlock<EnderEchoTunerBlock> ENDER_ECHO_TUNER = BLOCKS.register("ender_echo_tuner", EnderEchoTunerBlock::new);
    public static final DeferredBlock<EnderEchoCristalBlock> ENDER_ECHO_CRISTAL = BLOCKS.register("ender_echo_cristal", EnderEchoCristalBlock::new);
    public static final DeferredBlock<CalibratedSculkShriekerBlock> CALIBRATED_SCULK_SHRIEKER = BLOCKS.register("calibrated_sculk_shrieker",CalibratedSculkShriekerBlock::new);
    public static final DeferredBlock<EchoDruseBlock> ECHO_DRUSE = BLOCKS.register("echo_druse_block", EchoDruseBlock::new);
    public static final DeferredBlock<SculkWhisperBlock> SCULK_WHISPER = BLOCKS.register("sculk_whisper", SculkWhisperBlock::new);
}