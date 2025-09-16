package com.unddefined.enderechoing.effects;

import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class StaggerEffect extends MobEffect {
    //踉跄
    public StaggerEffect() {
        super(MobEffectCategory.HARMFUL, 0x8B4513); // 有害效果，棕色
    }

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, EnderEchoing.MODID);
    public static final DeferredHolder<MobEffect, StaggerEffect> STAGGER = MOB_EFFECTS.register("stagger",
            StaggerEffect::new);

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // 每tick应用效果
        if (!entity.onGround()) {
            RandomSource random = entity.getRandom();
            // 等级越高摔倒概率越高

            if (random.nextDouble() <  0.1 * (amplifier + 1)) {
                // 取消跳跃并施加向下的力模拟摔倒
                entity.setOnGround(false);
                entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.3, 0));
            }
        }
        final float random = entity.getRandom().nextFloat();
        if (random < 0.1f) {
            double x = entity.getRandom().nextDouble() * 0.3 - 0.15;
            double z = entity.getRandom().nextDouble() * 0.3 - 0.15;
            entity.setDeltaMovement(entity.getDeltaMovement().add(x, 0, z));
        }
        if (random < 0.1f) {
            float slowdownFactor = 0.9f - (amplifier * random);
            entity.setSpeed(entity.getSpeed() * slowdownFactor);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {

        return duration % 20 == 0;
    }


}
