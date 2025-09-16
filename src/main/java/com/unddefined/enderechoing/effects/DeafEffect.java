package com.unddefined.enderechoing.effects;

import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = EnderEchoing.MODID)
public class DeafEffect extends MobEffect {
    public DeafEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, "enderechoing");
    public static final DeferredHolder<MobEffect, DeafEffect> DEAFNESS = MOB_EFFECTS.register("deafness",
            () -> new DeafEffect(MobEffectCategory.HARMFUL, 0x696969));

    @SubscribeEvent
    public static void onSoundPlay(PlaySoundEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (player.hasEffect(DeafEffect.DEAFNESS)) {
                // 完全阻止声音播放
                event.setSound(null);
            }

        }
    }
}