package com.unddefined.enderechoing.server.mixin;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static com.unddefined.enderechoing.server.registry.DataRegistry.ENDER_EYE_OWNER;

@Mixin(EndCrystal.class)
public abstract class EndCrystalMixin {
    @Inject(method = "defineSynchedData", at = @At("HEAD"))
    protected void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(ENDER_EYE_OWNER, Optional.empty());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        EndCrystal self = (EndCrystal) (Object) this;
        if (!(self.level() instanceof ServerLevel level)) return;

        var tag = self.getEntityData();
        if (tag.get(ENDER_EYE_OWNER).isEmpty()) return;
        var player = level.getPlayerByUUID(tag.get(ENDER_EYE_OWNER).get());
        if (player == null || player.distanceToSqr(self) > 16 * 16 || player.getHealth() >= player.getMaxHealth())
            tag.set(ENDER_EYE_OWNER, Optional.empty());

    }
}
