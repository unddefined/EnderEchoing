package com.unddefined.enderechoing.server.mixin;

import com.unddefined.enderechoing.entities.ai.EndermanCuriousAboutPlayerGoal;
import com.unddefined.enderechoing.server.registry.ItemRegistry;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;

@Mixin(EnderMan.class)
public class EnderManMixin {
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        EnderMan enderman = (EnderMan) (Object) this;
        enderman.goalSelector.addGoal(8, new EndermanCuriousAboutPlayerGoal(enderman));
    }

    @Inject(method = "isLookingAtMe", at = @At("HEAD"), cancellable = true)
    private void isLookingAtMe(Player player, CallbackInfoReturnable<Boolean> cir) {
        CuriosApi.getCuriosInventory(player).flatMap(handler -> handler.findCurios(ItemRegistry.ENDER_ECHOING_EYE.get())
                .stream().findFirst()).ifPresent(slot -> cir.setReturnValue(false));
    }

}
