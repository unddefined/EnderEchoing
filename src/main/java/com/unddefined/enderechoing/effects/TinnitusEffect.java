package com.unddefined.enderechoing.effects;

import com.unddefined.enderechoing.client.ModSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TinnitusEffect extends MobEffect {
    //耳鸣
    protected TinnitusEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF);
    }

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, "enderechoing");
    public static final DeferredHolder<MobEffect, TinnitusEffect> TINNITUS = MOB_EFFECTS.register("tinnitus",
            TinnitusEffect::new);

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        Minecraft mc = Minecraft.getInstance();
        // 仅在客户端侧处理音效
        SimpleSoundInstance tinnitusSound = new SimpleSoundInstance(
                ModSoundEvents.TINNITUS.get(),
                SoundSource.AMBIENT,
                10.0F, // 音量
                1.0F, // 音调
                net.minecraft.util.RandomSource.create(),
                entity.getX(), entity.getY(), entity.getZ()
        );
        if (!mc.getSoundManager().isActive(tinnitusSound)) {
            mc.getSoundManager().play(tinnitusSound);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true; // 每tick都触发效果以保持音效播放
    }
}