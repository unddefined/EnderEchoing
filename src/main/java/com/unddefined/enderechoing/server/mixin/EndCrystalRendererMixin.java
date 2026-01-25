package com.unddefined.enderechoing.server.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.unddefined.enderechoing.server.registry.DataRegistry.ENDER_EYE_OWNER;
import static net.minecraft.client.renderer.entity.EndCrystalRenderer.getY;

@Mixin(EndCrystalRenderer.class)
public abstract class EndCrystalRendererMixin {
    @Inject(method = "render*", at = @At(value = "TAIL"))
    public void render(EndCrystal entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        var tag = entity.getEntityData();
        if (tag.get(ENDER_EYE_OWNER).isPresent()) {
            var player = entity.level().getPlayerByUUID(tag.get(ENDER_EYE_OWNER).get());
            if (player != null && player.distanceToSqr(entity) < 16 * 16 && player.getHealth() < player.getMaxHealth()) {
                float dx = (float) (player.getX() - entity.getX());
                float dy = (float) (player.getY() - entity.getY() - player.getBbHeight() * 0.5);
                float dz = (float) (player.getZ() - entity.getZ());
                poseStack.translate(dx, dy, dz);
                EnderDragonRenderer
                        .renderCrystalBeams(-dx, -dy + getY(entity, partialTicks), -dz, partialTicks, entity.time, poseStack, buffer, packedLight);
            }
        }
    }
}
