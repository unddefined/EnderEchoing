package com.unddefined.enderechoing.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class ParticleMethods {
    public static void spawnInfrasoundParticles(ClientLevel level, Vec3 center, float radius, boolean isStatic) {
        RandomSource random = level.random;

        // 主要粒子颜色 #111b21 (深蓝绿色)
        final float primaryR = 0x11 / 255.0f;
        final float primaryG = 0x1b / 255.0f;
        final float primaryB = 0x21 / 255.0f;

        // 次要粒子颜色 #0b5464 (深青色)
        final float secondaryR = 0x0b / 255.0f;
        final float secondaryG = 0x54 / 255.0f;
        final float secondaryB = 0x64 / 255.0f;

        // 第三种粒子颜色 #29dfeb (亮青色)
        final float tertiaryR = 0x29 / 255.0f;
        final float tertiaryG = 0xdf / 255.0f;
        final float tertiaryB = 0xeb / 255.0f;

        // 粒子总数基于半径计算
        int particleCount = (int) (Math.PI * radius * radius * 20);

        for (int i = 0; i < particleCount; i++) {
            if (!isStatic) {
                double angle = random.nextDouble() * 2 * Math.PI;
                double distance = random.nextDouble() * radius;

                double startX = center.x + Math.cos(angle) * distance * 0.5;
                double startZ = center.z + Math.sin(angle) * distance * 0.5;

                double endX = center.x + Math.cos(angle) * radius;
                double endZ = center.z + Math.sin(angle) * radius;

                // 根据距离确定粒子类型
                float particleSelector = random.nextFloat();

                if (particleSelector < 0.80) {
                    level.addParticle(new DirectlyMovingDustOptions(80, primaryR, primaryG, primaryB, 1F),
                            startX, center.y, startZ, endX, center.y, endZ);
                } else if (particleSelector < 0.90) {
                    level.addParticle(new DirectlyMovingDustOptions(80, secondaryR, secondaryG, secondaryB, 1F),
                            startX, center.y, startZ, endX, center.y, endZ);
                } else {
                    level.addParticle(new DirectlyMovingDustOptions(80, tertiaryR, tertiaryG, tertiaryB, 1F),
                            startX, center.y, startZ, endX, center.y, endZ);
                }
            } else {
                // 生成球形分布的静态粒子
                // 使用球坐标生成均匀分布的点
                double theta = random.nextDouble() * 2 * Math.PI; // 方位角
                double phi = Math.acos(2 * random.nextDouble() - 1); // 极角

                double x = center.x + radius * Math.sin(phi) * Math.cos(theta);
                double y = center.y + radius * Math.cos(phi) + 0.4;
                double z = center.z + radius * Math.sin(phi) * Math.sin(theta);

                // 根据位置确定粒子类型
                float particleSelector = random.nextFloat();

                if (particleSelector < 0.90-radius) {
                    level.addParticle(new DirectlyMovingDustOptions(20, primaryR, primaryG, primaryB, 0.1F),
                            x, y, z, x, y, z);
                } else if (particleSelector < 0.99-radius) {
                    level.addParticle(new DirectlyMovingDustOptions(20, secondaryR, secondaryG, secondaryB, 0.1F),
                            x, y, z, x, y, z);
                } else {
                    level.addParticle(new DirectlyMovingDustOptions(20, tertiaryR, tertiaryG, tertiaryB, 0.1F),
                            x, y, z, x, y, z);
                }
            }
        }
    }
}
