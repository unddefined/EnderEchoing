package com.unddefined.enderechoing.server.registry;

import com.unddefined.enderechoing.entities.EnderEchoCrystalEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.unddefined.enderechoing.EnderEchoing.MODID;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);

    public static final DeferredHolder<EntityType<?>,EntityType<EnderEchoCrystalEntity>> ENDER_ECHO_CRYSTAL_ENTITY = ENTITIES.register("ender_echo_crystal_entity", () ->
                    EntityType.Builder.<EnderEchoCrystalEntity>of(EnderEchoCrystalEntity::new, MobCategory.MISC).fireImmune()
                            .sized(1.0F, 1.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE).build("ender_echo_crystal_entity")
            );

}
