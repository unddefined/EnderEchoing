package com.unddefined.enderechoing.server.registry;

import com.unddefined.enderechoing.EnderEchoing;
import com.unddefined.enderechoing.effects.AttackScatteredEffect;
import com.unddefined.enderechoing.effects.DeafEffect;
import com.unddefined.enderechoing.effects.StaggerEffect;
import com.unddefined.enderechoing.effects.TinnitusEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MobEffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, EnderEchoing.MODID);
    public static final DeferredHolder<MobEffect, AttackScatteredEffect> ATTACK_SCATTERED =
            MOB_EFFECTS.register("attack_scattered",AttackScatteredEffect::new);
    public static final DeferredHolder<MobEffect, TinnitusEffect> TINNITUS = MOB_EFFECTS.register("tinnitus",
            TinnitusEffect::new);
    public static final DeferredHolder<MobEffect, StaggerEffect> STAGGER = MOB_EFFECTS.register("stagger", StaggerEffect::new);
    public static final DeferredHolder<MobEffect, DeafEffect> DEAFNESS = MOB_EFFECTS.register("deafness",
            DeafEffect::new);
}
