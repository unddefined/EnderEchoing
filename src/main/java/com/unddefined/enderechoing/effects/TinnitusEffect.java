package com.unddefined.enderechoing.effects;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.client.ModSoundEvents;
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

import static net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE;

public class TinnitusEffect extends MobEffect {
    //耳鸣
    protected TinnitusEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF);
    }
    public static final ResourceLocation tinnitus_modifier_id = ResourceLocation.fromNamespaceAndPath(EnderEchoing.MODID, "tinnitus");
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, "enderechoing");
    public static final DeferredHolder<MobEffect, TinnitusEffect> TINNITUS = MOB_EFFECTS.register("tinnitus",
            TinnitusEffect::new);

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            // 为玩家播放耳鸣声
            player.playSound(ModSoundEvents.TINNITUS.get(), 1.0F, 1.0F);
        } else if (entity instanceof Monster monster) {
            // 扰乱怪物的听觉和视觉范围
            AttributeModifier modifier = new AttributeModifier(tinnitus_modifier_id, -entity.getRandom().nextInt(3), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            monster.getAttribute(FOLLOW_RANGE).addOrUpdateTransientModifier(modifier);

        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }
}