package com.unddefined.enderechoing.registry;

import com.unddefined.enderechoing.items.EnderEchoingCore;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("enderechoing");

    public static final DeferredItem<BlockItem> ENDER_ECHOIC_TELEPORTER_ITEM = ITEMS.registerSimpleBlockItem("ender_echoic_teleporter", BlockRegistry.ENDER_ECHOIC_TELEPORTER);
    public static final DeferredItem<BlockItem> CALIBRATED_SCULK_SHRIEKER_ITEM = ITEMS.registerSimpleBlockItem("calibrated_sculk_shrieker", BlockRegistry.CALIBRATED_SCULK_SHRIEKER);
    public static final DeferredItem<Item> ENDER_ECHOING_CORE = ITEMS.registerItem("ender_echoing_core", EnderEchoingCore::new);
}