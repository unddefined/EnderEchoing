package com.unddefined.enderechoing;

import com.mojang.logging.LogUtils;
import com.unddefined.enderechoing.registry.BlockEntityRegistry;
import com.unddefined.enderechoing.registry.BlockRegistry;
import com.unddefined.enderechoing.registry.CreativeModeTabRegistry;
import com.unddefined.enderechoing.registry.ItemRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

// The @Mod annotation tells the loader that this class is the main mod class.
// The mod id is defined in mods.toml and must match the modId field below.
@Mod(EnderEchoing.MODID)
public class EnderEchoing {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "enderechoing";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public EnderEchoing(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        BlockRegistry.BLOCKS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        CreativeModeTabRegistry.CREATIVE_MODE_TABS.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }
}