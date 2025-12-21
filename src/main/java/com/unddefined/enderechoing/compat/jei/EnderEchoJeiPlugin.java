package com.unddefined.enderechoing.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.unddefined.enderechoing.EnderEchoing.MODID;

@JeiPlugin
public class EnderEchoJeiPlugin implements IModPlugin {
    public static IJeiRuntime RUNTIME;

    public static ItemStack getItemFromJei() {
        if (RUNTIME == null) return ItemStack.EMPTY;
        var overlay = RUNTIME.getIngredientListOverlay();

        if (!overlay.isListDisplayed()) return ItemStack.EMPTY;

        return overlay.getIngredientUnderMouse().flatMap(ITypedIngredient::getItemStack).orElse(ItemStack.EMPTY);
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(MODID, "jei");
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime runtime) {
        RUNTIME = runtime;
    }
}

