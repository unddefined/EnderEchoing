package com.unddefined.enderechoing.server.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.unddefined.enderechoing.server.registry.MobEffectRegistry.SCULK_VEIL;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "dampensVibrations", at = @At("HEAD"), cancellable = true)
    private void dampensVibrations(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player)
            if (player.hasEffect(SCULK_VEIL))
                cir.setReturnValue(true);
    }
}

