package com.unddefined.enderechoing.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("enderechoing");

//    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
//            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    public static final DeferredItem<BlockItem> ENDER_ECHOIC_TELEPORTER_ITEM = ITEMS.registerSimpleBlockItem("ender_echoic_teleporter", BlockRegistry.ENDER_ECHOIC_TELEPORTER);
}