package com.unddefined.enderechoing.server;

import com.unddefined.enderechoing.EnderEchoing;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import java.util.Objects;

import static com.unddefined.enderechoing.effects.AttackScatteredEffect.attack_scattered_modifier_id;
import static com.unddefined.enderechoing.effects.StaggerEffect.stagger_modifier_id;
import static com.unddefined.enderechoing.effects.TinnitusEffect.tinnitus_modifier_id;
import static com.unddefined.enderechoing.server.registry.MobEffectRegistry.*;
import static net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE;
import static net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED;

@EventBusSubscriber(modid = EnderEchoing.MODID)
public class ServerEffect {
    @SubscribeEvent
    public static void onExpireEffect(MobEffectEvent.Expired event) {
        if (!event.getEntity().hasEffect(TINNITUS)) {
            if (event.getEntity() instanceof Monster monster) {
                monster.getAttribute(FOLLOW_RANGE).removeModifier(tinnitus_modifier_id);
            }
        }
        if (!event.getEntity().hasEffect(STAGGER)) {
            Objects.requireNonNull(event.getEntity().getAttribute(MOVEMENT_SPEED)).removeModifier(stagger_modifier_id);
        }
        if (!event.getEntity().hasEffect(ATTACK_SCATTERED)) {
            Objects.requireNonNull(event.getEntity().getAttribute(MOVEMENT_SPEED)).removeModifier(attack_scattered_modifier_id);
        }

    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        if (player.hasEffect(STAGGER)) {
            // 获取当前移动输入
            var movement = event.getInput();

            // 获取效果等级（用于确定偏移程度）
            int amplifier = player.getEffect(STAGGER).getAmplifier();

            // 随机偏移移动方向
            RandomSource random = player.getRandom();
            float offsetStrength = 0.1f * (amplifier + 1); // 等级越高偏移越严重

            // 添加随机偏移
            movement.forwardImpulse += (random.nextFloat() - 0.5f) * offsetStrength;
            movement.leftImpulse += (random.nextFloat() - 0.5f) * offsetStrength;
        }

    }

    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(ATTACK_SCATTERED)) {
            // 获取效果实例
            MobEffectInstance effectInstance = entity.getEffect(ATTACK_SCATTERED);
            if (effectInstance != null) {
                int amplifier = effectInstance.getAmplifier();

                // 根据效果等级有概率取消攻击
                RandomSource random = entity.getRandom();
                float chance = 0.3f * (amplifier + 1); // 每级增加5%的概率
                if (random.nextFloat() < chance) {
                    // 取消攻击
                    event.setCanceled(true);
                }
            }
        }
    }
}