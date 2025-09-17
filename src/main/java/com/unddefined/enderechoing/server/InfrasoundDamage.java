package com.unddefined.enderechoing.server;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.unddefined.enderechoing.effects.AttackScatteredEffect.ATTACK_SCATTERED;
import static com.unddefined.enderechoing.effects.StaggerEffect.STAGGER;
import static com.unddefined.enderechoing.effects.TinnitusEffect.TINNITUS;
import static net.minecraft.world.effect.MobEffects.CONFUSION;
import static net.minecraft.world.effect.MobEffects.WEAKNESS;

public class InfrasoundDamage extends DamageSource {
    public static final ResourceKey<DamageType> INFRASOUND_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("enderechoing", "infrasound_damage"));

    public InfrasoundDamage(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition) {
        super(type, directEntity, causingEntity, damageSourcePosition);
    }

    public static void InfrasoundBurst(Level level, Vec3 center, float hurt_range, float effect_range, Entity causingEntity) {
        if (level.isClientSide()) return;

        // 获取范围内的所有生物实体
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class,
                net.minecraft.world.phys.AABB.ofSize(center, hurt_range, hurt_range, hurt_range));

        for (LivingEntity entity : entities) {
            // 计算实体与中心点的距离
            double distance = entity.distanceToSqr(center);
            double distanceSqrt = Math.sqrt(distance);

            // 对在半径hurt_range范围内的生物造成真实伤害
            if (distanceSqrt <= effect_range) {
                // 创建伤害源
                Holder<DamageType> damageTypeHolder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(INFRASOUND_DAMAGE);
                DamageSource damageSource = new InfrasoundDamage(damageTypeHolder, null, causingEntity, center);

                // 造成hurt_range点真实伤害（忽略护甲）
                entity.hurt(damageSource, hurt_range);
            }

            // 对在effect_range范围内的生物应用debuff效果
            if (distanceSqrt <= effect_range) {
                // 计算持续时间 = effect_range - 与中心的距离
                int duration = (int) (effect_range - distanceSqrt);

                // 应用多种debuff效果
                entity.addEffect(new MobEffectInstance(ATTACK_SCATTERED, duration * 20, 1));
                entity.addEffect(new MobEffectInstance(STAGGER, duration * 20, 1));
                entity.addEffect(new MobEffectInstance(TINNITUS, duration * 20, 1));
                // 添加反胃和虚弱效果
                entity.addEffect(new MobEffectInstance(CONFUSION, duration * 20, 1));
                entity.addEffect(new MobEffectInstance(WEAKNESS, duration * 20, 1));
            }
        }

        // 添加粒子效果
        spawnInfrasoundParticles((ServerLevel) level, center, effect_range);
    }

    /**
     * 生成次声波粒子效果
     * @param level 服务端世界
     * @param center 粒子效果中心点
     * @param maxRadius 粒子效果最大半径
     */
    private static void spawnInfrasoundParticles(ServerLevel level, Vec3 center, float maxRadius) {
//        RandomSource random = level.getRandom();
//
//        // 创建三种不同颜色的粒子选项
//        ColorParticleOption mainColor = new ColorParticleOption(ParticleTypes.SPELL_MOB, 0.066f, 0.106f, 0.129f, 1.0f); // #111b21
//        ColorParticleOption secondaryColor = new ColorParticleOption(ParticleTypes.SPELL_MOB, 0.043f, 0.329f, 0.392f, 1.0f); // #0b5464
//        ColorParticleOption accentColor = new ColorParticleOption(ParticleTypes.SPELL_MOB, 0.161f, 0.875f, 0.922f, 1.0f); // #29dfeb
//
//        // 创建多层波纹效果
//        for (double radius = 0.5; radius <= maxRadius; radius += 0.5) {
//            int particlesPerRing = (int) (radius * 8); // 随半径增加粒子数量
//
//            for (int i = 0; i < particlesPerRing; i++) {
//                double angle = (i * 2 * Math.PI) / particlesPerRing;
//
//                // 计算粒子位置，加入一些随机偏移
//                double offsetX = Math.cos(angle) * radius + (random.nextDouble() - 0.5) * 0.3;
//                double offsetZ = Math.sin(angle) * radius + (random.nextDouble() - 0.5) * 0.3;
//                double offsetY = random.nextDouble() * 0.5; // 垂直方向的随机偏移
//
//                Vec3 particlePos = center.add(offsetX, offsetY, offsetZ);
//
//                // 根据距离计算消散程度
//                double fadeAmount = 1.0 - (radius / maxRadius);
//
//                // 主要颜色粒子
//                level.sendParticles(mainColor,
//                        particlePos.x, particlePos.y, particlePos.z,
//                        1, // 粒子数量
//                        0, 0, 0, // 速度
//                        0.1 * fadeAmount); // 速度系数
//
//                // 随机添加次要颜色和强调色粒子
//                if (random.nextFloat() < 0.3 * fadeAmount) {
//                    level.sendParticles(secondaryColor,
//                            particlePos.x, particlePos.y, particlePos.z,
//                            1, 0, 0, 0, 0.1 * fadeAmount);
//                }
//
//                if (random.nextFloat() < 0.2 * fadeAmount) {
//                    level.sendParticles(accentColor,
//                            particlePos.x, particlePos.y, particlePos.z,
//                            1, 0, 0, 0, 0.1 * fadeAmount);
//                }
//            }
//        }
    }
}