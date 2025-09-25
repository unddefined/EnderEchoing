package com.unddefined.enderechoing.client;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.renderer.layer.SculkVeilLayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = EnderEchoing.MODID, value = Dist.CLIENT)
public class ClientEffect {

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        // 为玩家渲染器添加影匿渲染层
        event.getSkins().forEach((skin) -> {
            EntityRenderer<? extends Player> playerRenderer = event.getSkin(skin);
            if (playerRenderer instanceof PlayerRenderer renderer) {
                renderer.addLayer(new SculkVeilLayer(renderer));
            }
        });
    }
}