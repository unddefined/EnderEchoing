package com.unddefined.enderechoing;

import com.mojang.logging.LogUtils;
import com.unddefined.enderechoing.client.ModSoundEvents;
import com.unddefined.enderechoing.client.gui.TunerMenu;
import com.unddefined.enderechoing.server.registry.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The @Mod annotation tells the loader that this class is the main mod class.
// The mod id is defined in mods.toml and must match the modId field below.
@Mod(EnderEchoing.MODID)
public class EnderEchoing {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "enderechoing";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MODID);
    public static final Supplier<MenuType<TunerMenu>> TUNER_MENU = MENUS.register("tuner_menu", () -> IMenuTypeExtension.create(TunerMenu::new));
    public EnderEchoing(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        BlockRegistry.BLOCKS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        EntityRegistry.ENTITIES.register(modEventBus);
        CreativeModeTabRegistry.CREATIVE_MODE_TABS.register(modEventBus);
        MobEffectRegistry.MOB_EFFECTS.register(modEventBus);
        ModSoundEvents.SOUND_EVENTS.register(modEventBus);
        ParticlesRegistry.PARTICLE_TYPES.register(modEventBus);
        DataRegistry.COMPONENT_TYPES.register(modEventBus);
        DataRegistry.ATTACHMENT_TYPES.register(modEventBus);
        MENUS.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }
}