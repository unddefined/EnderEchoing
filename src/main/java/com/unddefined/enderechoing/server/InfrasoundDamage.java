package com.unddefined.enderechoing.server;

import com.unddefined.enderechoing.network.packet.InfrasoundParticlePacket;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.unddefined.enderechoing.server.registry.MobEffectRegistry.*;
import static net.minecraft.world.effect.MobEffects.WEAKNESS;

public class InfrasoundDamage extends DamageSource {
    public static final ResourceKey<DamageType> INFRASOUND_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("enderechoing", "infrasound_damage"));

    public InfrasoundDamage(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition) {
        super(type, directEntity, causingEntity, damageSourcePosition);
    }

    public static void InfrasoundBurst(ServerLevel level, Vec3 center, float hurt_range, float effect_range, int damage, Entity causingEntity) {
        // 获取范围内的所有生物实体
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class,
                net.minecraft.world.phys.AABB.ofSize(center, effect_range * 2, effect_range * 2, effect_range * 2));

        for (LivingEntity entity : entities) {
            // 计算实体与中心点的距离
            double distanceSqrt = entity.position().distanceTo(center);

            // 对在半径hurt_range范围内的生物造成真实伤害
            if (distanceSqrt <= hurt_range) {
                // 创建伤害源
                Holder<DamageType> damageTypeHolder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(INFRASOUND_DAMAGE);
                DamageSource damageSource = new InfrasoundDamage(damageTypeHolder, null, causingEntity, center);

                // 造成effect_range点真实伤害（忽略护甲）
                entity.hurt(damageSource, damage);
            }

            // 对在effect_range范围内的生物应用debuff效果
            if (distanceSqrt <= effect_range) {
                // 计算持续时间 = effect_range - 与中心的距离
                int duration = (int) (effect_range - distanceSqrt);

                // 应用多种debuff效果
                entity.addEffect(new MobEffectInstance(ATTACK_SCATTERED, duration * 20, 1));
                entity.addEffect(new MobEffectInstance(STAGGER, duration * 20, 1));
                entity.addEffect(new MobEffectInstance(TINNITUS, duration * 20, 1));
//                entity.addEffect(new MobEffectInstance(CONFUSION, duration * 20, 1));
                entity.addEffect(new MobEffectInstance(WEAKNESS, duration * 20, 1));
            }
        }
        // 向所有客户端发送粒子效果数据包
        PacketDistributor.sendToAllPlayers(new InfrasoundParticlePacket(center, effect_range, false));
    }


}