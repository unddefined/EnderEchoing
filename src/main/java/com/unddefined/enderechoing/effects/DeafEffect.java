package com.unddefined.enderechoing.effects;

import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;

import static com.unddefined.enderechoing.server.registry.MobEffectRegistry.DEAFNESS;

@EventBusSubscriber(modid = EnderEchoing.MODID)
public class DeafEffect extends MobEffect {
    public DeafEffect() {
        super(MobEffectCategory.HARMFUL, 0x696969);
    }
    @SubscribeEvent
    public static void onSoundPlay(PlaySoundEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (player.hasEffect(DEAFNESS)) {
                // 完全阻止声音播放
                event.setSound(null);
            }

        }
    }
}