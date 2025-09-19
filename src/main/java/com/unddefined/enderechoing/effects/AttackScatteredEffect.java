package com.unddefined.enderechoing.effects;

import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED;
import static net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET;

public class AttackScatteredEffect extends MobEffect {
    //攻击失调
    public AttackScatteredEffect() {
        super(MobEffectCategory.HARMFUL, 0x808080);
    }
    public static final ResourceLocation attack_scattered_modifier_id = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "attack_scattered");
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, EnderEchoing.MODID);
    public static final DeferredHolder<MobEffect, AttackScatteredEffect> ATTACK_SCATTERED = MOB_EFFECTS.register("attack_scattered",
            AttackScatteredEffect::new);

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.releaseUsingItem();
        if (entity instanceof Monster monster && entity.getRandom().nextFloat()<=0.7f) {
            // 强制清除目标
            monster.setTarget(null);
//            // 清除记忆中的攻击目标
            monster.setLastHurtByMob(null);
            monster.setLastHurtByPlayer(null);
            monster.getNavigation().stop();
            monster.getBrain().eraseMemory(ATTACK_TARGET);

        }
        if (entity instanceof Player player) {
            AttributeModifier modifier = new AttributeModifier(attack_scattered_modifier_id, -entity.getRandom().nextFloat(), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            player.getAttribute(ATTACK_SPEED).addOrUpdateTransientModifier(modifier);
        }


        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {

        return duration % 20 == 0;
    }

}