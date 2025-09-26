package com.unddefined.enderechoing.effects;

import com.unddefined.enderechoing.server.registry.MobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class SculkVeilEffect extends MobEffect {
    public SculkVeilEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x4215441);
    }

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int pAmplifier) {

        // 检查实体是否发光，如果发光则不应用影匿效果
        if (livingEntity.isCurrentlyGlowing()) {
            // 直接移除刚刚添加的效果
            livingEntity.removeEffect(MobEffectRegistry.SCULK_VEIL);
            return;
        }
        // 获取当前效果实例并确保不为null
        var effectInstance = livingEntity.getEffect(MobEffectRegistry.SCULK_VEIL);
        int duration = 60;
        if (effectInstance != null) duration = Math.max(duration, effectInstance.getDuration());

        MobEffectInstance WEAKNESS = new MobEffectInstance(MobEffects.WEAKNESS, duration);
        MobEffectInstance DIG_SLOWDOWN = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration);
        MobEffectInstance HUNGER = new MobEffectInstance(MobEffects.HUNGER, duration);
        MobEffectInstance MOVEMENT_SLOWDOWN = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration);
        MobEffectInstance BLINDNESS = new MobEffectInstance(MobEffects.BLINDNESS, duration);
        MobEffectInstance DEAFNESS = new MobEffectInstance(MobEffectRegistry.DEAFNESS, duration);

        // 确保只添加两个不同的随机效果
        int firstEffectIndex = livingEntity.getRandom().nextInt(6);
        int secondEffectIndex;
        do {
            secondEffectIndex = livingEntity.getRandom().nextInt(6);
        } while (secondEffectIndex == firstEffectIndex);

        MobEffectInstance[] effects = {WEAKNESS, DIG_SLOWDOWN, HUNGER, MOVEMENT_SLOWDOWN, BLINDNESS, DEAFNESS};
        livingEntity.addEffect(effects[firstEffectIndex]);
        livingEntity.addEffect(effects[secondEffectIndex]);

        livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 300));

        var targetingCondition = TargetingConditions.forCombat().ignoreLineOfSight().
            selector(e -> (((Mob) e).getTarget() == livingEntity));

        //remove aggro from anything targeting us
        livingEntity.level().getNearbyEntities(Mob.class, targetingCondition, livingEntity, livingEntity.getBoundingBox().inflate(40D))
            .forEach(entityTargetingCaster -> {
                entityTargetingCaster.setTarget(null);
                entityTargetingCaster.targetSelector.getAvailableGoals().forEach(WrappedGoal::stop);
                entityTargetingCaster.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            });
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {return true;}

    @Override
    public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
        // 检查实体是否发光，如果发光则取消影匿效果
        return !entity.isCurrentlyGlowing();
        //TODO: 半透明+粒子环绕
//        pLivingEntity.setInvisible(true);

    }
}