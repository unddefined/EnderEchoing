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

public class ShadowVeilEffect extends MobEffect {
    public ShadowVeilEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x4215441);
    }
    int lastHurtTimestamp;
    // 用于防止onEffectAdded被多次调用的标记
    private static final String TAG_SHADOW_VEIL_APPLIED = "ShadowVeilApplied";

    @Override
    public void onEffectAdded(LivingEntity livingEntity, int pAmplifier) {
        // 检查是否已经应用过效果，防止重复调用
        if (livingEntity.getPersistentData().getBoolean(TAG_SHADOW_VEIL_APPLIED)) return;
        // 设置标记，表示已经应用过效果
        livingEntity.getPersistentData().putBoolean(TAG_SHADOW_VEIL_APPLIED, true);

        // 检查实体是否发光，如果发光则不应用影匿效果
        if (livingEntity.isCurrentlyGlowing()) {
            // 直接移除刚刚添加的效果
            livingEntity.removeEffect(MobEffectRegistry.SHADOW_VEIL);
            // 清除标记
            livingEntity.getPersistentData().remove(TAG_SHADOW_VEIL_APPLIED);
            return;
        }
        // 获取当前效果实例并确保不为null
        var effectInstance = livingEntity.getEffect(MobEffectRegistry.SHADOW_VEIL);
        int duration = 60; // 默认基础持续时间
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
        } while (secondEffectIndex == firstEffectIndex); // 确保两个效果不同

        MobEffectInstance[] effects = {WEAKNESS, DIG_SLOWDOWN, HUNGER, MOVEMENT_SLOWDOWN, BLINDNESS, DEAFNESS};
        livingEntity.addEffect(effects[firstEffectIndex]);
        livingEntity.addEffect(effects[secondEffectIndex]);

        // 添加黑暗效果
        livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 400));

        var targetingCondition = TargetingConditions.forCombat().ignoreLineOfSight().
            selector(e -> (((Mob) e).getTarget() == livingEntity));

        //remove aggro from anything targeting us
        livingEntity.level().getNearbyEntities(Mob.class, targetingCondition, livingEntity, livingEntity.getBoundingBox().inflate(40D))
            .forEach(entityTargetingCaster -> {
                entityTargetingCaster.setTarget(null);
                entityTargetingCaster.targetSelector.getAvailableGoals().forEach(WrappedGoal::stop);
                entityTargetingCaster.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
            });
        this.lastHurtTimestamp = livingEntity.getLastHurtMobTimestamp();

    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {return true;}

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        // 检查实体是否发光，如果发光则取消影匿效果
        if (pLivingEntity.isCurrentlyGlowing()) {
            // 清除标记
            pLivingEntity.getPersistentData().remove(TAG_SHADOW_VEIL_APPLIED);
            return false; // 返回false会移除效果
        }
        //If we attack, we lose invis
        //TODO: can be optimized via use of event instead of checking every tick
        return pLivingEntity.level().isClientSide || lastHurtTimestamp == pLivingEntity.getLastHurtMobTimestamp();
    }
}