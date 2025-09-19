package com.unddefined.enderechoing;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);

    public static final ModConfigSpec.IntValue ENDER_ECHOING_CORE_COOLDOWN = BUILDER
            .comment("Cooldown time for the Ender Echoing Core in ticks (20 ticks = 1 second)")
            .defineInRange("EnderEchoingCoreCooldown", 100, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue ECHO_DRUSE_MAX_GROWTH_VALUE = BUILDER
            .comment( "Max growth value for the Echo Druse")
            .defineInRange("echo druse max growth value", 40000, 4, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue ECHO_DRUSE_GENERATION_PROBABILITY = BUILDER
            .comment( "Probability of Echo Druse block generation")
            .defineInRange("echo druse generation probability",  0.3, 0, Double.MAX_VALUE);
    public static final ModConfigSpec.IntValue SCULK_WHISPER_COOLDOWN = BUILDER
            .comment( "Cooldown of sculk shrieker's InfrasoundBurst")
            .defineInRange("sculk whisper cooldown",  900, 0, Integer.MAX_VALUE);
    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
    static final ModConfigSpec SPEC = BUILDER.build();
}