package com.unddefined.enderechoing.server.mixin;

import net.minecraft.client.particle.FlyTowardsPositionParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FlyTowardsPositionParticle.class)
public class FlyTowardsPositionParticleMixin {
    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 1.2F))
    private float modifyGravityFactor(float original) {return 1.8F;}
}