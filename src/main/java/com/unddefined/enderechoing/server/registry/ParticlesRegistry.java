package com.unddefined.enderechoing.server.registry;

import com.unddefined.enderechoing.client.particles.DirectlyMovingDustParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ParticlesRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "enderechoing");

    public static final DeferredHolder<ParticleType<?>, DirectlyMovingDustParticleType> DIRECT_MOVING_DUST = PARTICLE_TYPES.register(
            "direct_moving_dust", () -> new DirectlyMovingDustParticleType(false));
}