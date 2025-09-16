package com.unddefined.enderechoing.effects;

import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED;

public class AttackScatteredEffect extends MobEffect {
    //攻击失调
    public AttackScatteredEffect() {
        super(MobEffectCategory.HARMFUL, 0x808080);
    }

    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, EnderEchoing.MODID);
    public static final DeferredHolder<MobEffect, AttackScatteredEffect> ATTACK_SCATTERED = MOB_EFFECTS.register("attack_scattered",
            AttackScatteredEffect::new);

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getRandom().nextInt(8) == 0){
            entity.releaseUsingItem();
            if (entity instanceof Monster monster){
                monster.setTarget(null);
            }
        }
        if (entity instanceof Player player) player.getAttribute(ATTACK_SPEED).setBaseValue(entity.getRandom().nextFloat()+2.5f);

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {

        return duration % 20 == 0;
    }

}