package com.unddefined.enderechoing.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.gui.TransparentScreen;
import com.unddefined.enderechoing.client.renderer.block.CalibratedSculkShriekerRenderer;
import com.unddefined.enderechoing.client.renderer.block.EnderEchoicTeleporterRenderer;
import com.unddefined.enderechoing.registry.BlockEntityRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;


// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = EnderEchoing.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class EnderEchoingClient {
    private static final KeyMapping OPEN_TRANSPARENT_SCREEN = new KeyMapping(
            "key." + EnderEchoing.MODID + ".open_transparent_screen",
            InputConstants.KEY_R,
            "key.categories." + EnderEchoing.MODID
    );

    public EnderEchoingClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code

        // Register block entity renderers
        event.enqueueWork(() -> BlockEntityRenderers.register(BlockEntityRegistry.ENDER_ECHOIC_TELEPORTER.get(),
                context -> new EnderEchoicTeleporterRenderer()));
        event.enqueueWork(() -> BlockEntityRenderers.register(BlockEntityRegistry.CALIBRATED_SCULK_SHRIEKER.get(),
                context -> new CalibratedSculkShriekerRenderer()));
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_TRANSPARENT_SCREEN);
    }
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (OPEN_TRANSPARENT_SCREEN.consumeClick()) {
            // 获取Minecraft实例
            Minecraft minecraft = Minecraft.getInstance();
            // 确保玩家在游戏中且没有打开其他屏幕
            if (minecraft.player != null && minecraft.screen == null) {
                // 打开自定义屏幕
                minecraft.setScreen(new TransparentScreen());
            }
        }
    }

}