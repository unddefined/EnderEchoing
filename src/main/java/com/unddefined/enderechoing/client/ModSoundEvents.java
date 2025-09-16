package com.unddefined.enderechoing.client;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, "enderechoing");

    public static final DeferredHolder<SoundEvent, SoundEvent> TINNITUS = SOUND_EVENTS.register("tinnitus",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("enderechoing", "tinnitus")));

}
