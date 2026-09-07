package com.unddefined.enderechoing.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SculkIntrusionEffect extends MobEffect {
    public SculkIntrusionEffect() {
        super(MobEffectCategory.HARMFUL, 0x4215441);
    }
    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {return true;}

}
