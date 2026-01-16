package com.unddefined.enderechoing.server.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeModeTabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "enderechoing");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EnderEchoing = CREATIVE_MODE_TABS.register("enderechoing", () ->
        CreativeModeTab.builder()
            .title(Component.nullToEmpty("Ender Echoing"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ItemRegistry.ENDER_ECHOING_CORE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ItemRegistry.ENDER_ECHOING_CORE.get());
                output.accept(ItemRegistry.ENDER_ECHO_TUNE_CHAMBER.get());
                output.accept(ItemRegistry.ENDER_ECHO_CRYSTAL.get());
                output.accept(ItemRegistry.ECHO_DRUSE.get());
                output.accept(ItemRegistry.ENDER_ECHOING_PEARL.get());
                output.accept(ItemRegistry.ENDER_ECHOING_EYE.get());
                output.accept(ItemRegistry.CALIBRATED_SCULK_SHRIEKER_ITEM.get());
                output.accept(ItemRegistry.SCULK_WHISPER_ITEM.get());
                output.accept(ItemRegistry.ECHO_DRUSE_STAGE1_ITEM.get());
                output.accept(ItemRegistry.ECHO_DRUSE_STAGE2_ITEM.get());
                output.accept(ItemRegistry.ECHO_DRUSE_STAGE3_ITEM.get());
                output.accept(ItemRegistry.ECHO_DRUSE_STAGE4_ITEM.get());
            }).build());
}