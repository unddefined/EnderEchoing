package com.unddefined.enderechoing.registry;

import com.unddefined.enderechoing.blocks.EnderEchoicTeleporterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("enderechoing");

    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    
    public static final DeferredBlock<EnderEchoicTeleporterBlock> ENDER_ECHOIC_TELEPORTER = BLOCKS.register("ender_echoic_teleporter", EnderEchoicTeleporterBlock::new);
}