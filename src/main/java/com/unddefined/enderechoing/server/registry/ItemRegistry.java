package com.unddefined.enderechoing.server.registry;

import com.unddefined.enderechoing.blocks.EchoDruseBlock;
import com.unddefined.enderechoing.items.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("enderechoing");
    public static final DeferredItem<Item> ENDER_ECHOING_CORE = ITEMS.registerItem("ender_echoing_core", EnderEchoingCore::new);
    public static final DeferredItem<Item> ENDER_ECHO_TUNE_CHAMBER = ITEMS.registerItem("ender_echo_tune_chamber", EnderEchoTuneChamber::new);
    public static final DeferredItem<Item> ENDER_ECHO_CRYSTAL = ITEMS.registerItem("ender_echo_crystal", EnderEchoCrystal::new);
    public static final DeferredItem<Item> ECHO_DRUSE = ITEMS.registerItem("echo_druse", EchoDruse::new);
    public static final DeferredItem<Item> ENDER_ECHOING_PEARL = ITEMS.registerItem("ender_echoing_pearl", EnderEchoingPearl::new);
    public static final DeferredItem<Item> ENDER_ECHOING_EYE = ITEMS.registerItem("ender_echoing_eye", EnderEchoingEye::new);


    public static final DeferredItem<BlockItem> ENDER_ECHOIC_RESONATOR_ITEM = ITEMS.registerSimpleBlockItem("ender_echoic_resonator", BlockRegistry.ENDER_ECHOIC_RESONATOR);
    public static final DeferredItem<BlockItem> ENDER_ECHO_TUNER_ITEM = ITEMS.registerSimpleBlockItem("ender_echo_tuner", BlockRegistry.ENDER_ECHO_TUNER);
    public static final DeferredItem<BlockItem> CALIBRATED_SCULK_SHRIEKER_ITEM = ITEMS.registerSimpleBlockItem("calibrated_sculk_shrieker", BlockRegistry.CALIBRATED_SCULK_SHRIEKER);
    public static final DeferredItem<BlockItem> SCULK_WHISPER_ITEM = ITEMS.registerSimpleBlockItem("sculk_whisper", BlockRegistry.SCULK_WHISPER);
    //region ECHO_DRUSE_BLOCKITEM register
    public static final DeferredItem<BlockItem> ECHO_DRUSE_STAGE1_ITEM = ITEMS.register("echo_druse_stage1", () -> new BlockItem(BlockRegistry.ECHO_DRUSE.get(), new Item.Properties()) {
        @Override
        public BlockState getPlacementState(BlockPlaceContext context) {
            return BlockRegistry.ECHO_DRUSE.get().defaultBlockState().setValue(EchoDruseBlock.GROWTH_STAGE, 1);
        }
    });
    public static final DeferredItem<BlockItem> ECHO_DRUSE_STAGE2_ITEM = ITEMS.register("echo_druse_stage2", () -> new BlockItem(BlockRegistry.ECHO_DRUSE.get(), new Item.Properties()) {
        @Override
        public BlockState getPlacementState(BlockPlaceContext context) {
            return BlockRegistry.ECHO_DRUSE.get().defaultBlockState().setValue(EchoDruseBlock.GROWTH_STAGE, 2);
        }
    });
    public static final DeferredItem<BlockItem> ECHO_DRUSE_STAGE3_ITEM = ITEMS.register("echo_druse_stage3", () -> new BlockItem(BlockRegistry.ECHO_DRUSE.get(), new Item.Properties()) {
        @Override
        public BlockState getPlacementState(BlockPlaceContext context) {
            return BlockRegistry.ECHO_DRUSE.get().defaultBlockState().setValue(EchoDruseBlock.GROWTH_STAGE, 3);
        }
    });
    public static final DeferredItem<BlockItem> ECHO_DRUSE_STAGE4_ITEM = ITEMS.register("echo_druse_stage4", () -> new BlockItem(BlockRegistry.ECHO_DRUSE.get(), new Item.Properties()) {
        @Override
        public BlockState getPlacementState(BlockPlaceContext context) {
            return BlockRegistry.ECHO_DRUSE.get().defaultBlockState().setValue(EchoDruseBlock.GROWTH_STAGE, 4);
        }
    });
    //endregion

}