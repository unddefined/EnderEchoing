package com.unddefined.enderechoing.client;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.gui.screen.TunerScreen;
import com.unddefined.enderechoing.client.particles.ParticleDirectlyMovingDust;
import com.unddefined.enderechoing.client.renderer.block.*;
import com.unddefined.enderechoing.server.registry.BlockEntityRegistry;
import com.unddefined.enderechoing.server.registry.ParticlesRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.io.IOException;

import static com.unddefined.enderechoing.EnderEchoing.TUNER_MENU;


// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = EnderEchoing.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class EnderEchoingClient {
    private static final Minecraft mc = Minecraft.getInstance();
    public static PostChain sculkVeilPostChain = null;

    public EnderEchoingClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                sculkVeilPostChain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), ResourceLocation.fromNamespaceAndPath("enderechoing", "shaders/post/sculk_veil.json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Register block entity renderers
        event.enqueueWork(() -> BlockEntityRenderers.register(BlockEntityRegistry.ENDER_ECHOIC_RESONATOR.get(),
                context -> new EnderEchoicResonatorRenderer()));
        event.enqueueWork(() -> BlockEntityRenderers.register(BlockEntityRegistry.CALIBRATED_SCULK_SHRIEKER.get(),
                context -> new CalibratedSculkShriekerRenderer()));
        event.enqueueWork(() -> BlockEntityRenderers.register(BlockEntityRegistry.SCULK_WHISPER.get(),
                context -> new SculkWhisperRenderer()));
        event.enqueueWork(() -> BlockEntityRenderers.register(BlockEntityRegistry.ENDER_ECHO_TUNER.get(),
                context -> new EnderEchoTunerRenderer()));
        event.enqueueWork(() -> BlockEntityRenderers.register(BlockEntityRegistry.ENDER_ECHO_CRISTAL.get(),
                context -> new EnderEchoCristalBlockRenderer()));
    }
    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {event.register(TUNER_MENU.get(), TunerScreen::new);}

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticlesRegistry.DIRECT_MOVING_DUST.get(), ParticleDirectlyMovingDust.Provider::new);
    }

//    @SubscribeEvent
//    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
//        // 为玩家渲染器添加影匿渲染层（有bug）
//        event.getSkins().forEach((skin) -> {
//            EntityRenderer<? extends Player> playerRenderer = event.getSkin(skin);
//            if (playerRenderer instanceof PlayerRenderer renderer) {
//                renderer.addLayer(new SculkVeilLayer(renderer));
//            }
//        });
//    }
}