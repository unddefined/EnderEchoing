package com.unddefined.enderechoing;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    //TODO: 配置语言文件
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);

    public static final ModConfigSpec.IntValue ENDER_ECHOING_CORE_COOLDOWN = BUILDER
            .comment("Cooldown time for the Ender Echoing Core in seconds.")
            .defineInRange("EECore_Cooldown", 15, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue EECORE_TP_DISTANCE = BUILDER
            .comment("Interval distance for the Ender Echoing Core to teleport (each interval cost 1 ender echo pearl, half adjust)")
            .defineInRange("EECore_TP_Distance", 256, 64, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue EECrystal_TP_DISTANCE = BUILDER
            .comment("Max distance for the Ender Echo Crystal to teleport")
            .defineInRange("EECrystal_TP_Distance", 96, 16, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue EECrystal_HEAL_DISTANCE = BUILDER
            .comment("Max distance for the Ender Echo Crystal and End Crystal to heal the player")
            .defineInRange("EECrystal_heal_Distance", 16, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue EECrystal_HEAL_AMOUNT = BUILDER
            .comment("Amount for the Ender Echo Crystal to heal the player")
            .defineInRange("EECrystal_Heal_Amount", 2, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue EECrystal_HEAL_XP_COST = BUILDER
            .comment("XP cost for the Ender Echo Crystal to heal the player")
            .defineInRange("EECrystal_Heal_XPCost", 100, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue EECrystal_HEAL_INTERVAL = BUILDER
            .comment("Heal interval for the Ender Echo Crystal to heal the player")
            .defineInRange("EECrystal_Heal_Interval", 8, 1, Integer.MAX_VALUE);


    public static final ModConfigSpec.IntValue EndCrystal_HEAL_AMOUNT = BUILDER
            .comment("Amount for the End Crystal to heal the player")
            .defineInRange("End_Crystal_Heal_Amount", 1, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue EndCrystal_HEAL_XP_COST = BUILDER
            .comment("XP cost for the End Crystal to heal the player")
            .defineInRange("End_Crystal_Heal_XPCost", 100, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue EndCrystal_HEAL_INTERVAL = BUILDER
            .comment("Heal interval for the End Crystal to heal the player")
            .defineInRange("End_Crystal_Heal_Interval", 8, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SCULK_VEIL_DARKNESS_DURATION = BUILDER
            .comment("The duration of darkness effect which gives by sculk veil effect ")
            .defineInRange("sculk_veil_darkness_duration", 10, 20, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SCULK_VEIL_GLOWING_DURATION = BUILDER
            .comment("The duration of glowing effect which gives by sculk veil effect ")
            .defineInRange("sculk_veil_glowing_duration", 25, 20, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue EchoSoundingDistance = BUILDER
            .comment("Max distance for the ender echo sounding for other resonator.")
            .defineInRange("Echo_Sounding_Distance", 2048, 1, Integer.MAX_VALUE);


    public static final ModConfigSpec.IntValue ECHO_DRUSE_MAX_GROWTH_VALUE = BUILDER
            .comment( "Max growth value for the Echo Druse")
            .defineInRange("echo_druse_max_growth_value", 40000, 4, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue ECHO_DRUSE_GENERATION_PROBABILITY = BUILDER
            .comment( "Probability of Echo Druse block generation")
            .defineInRange("echo_druse_generation_probability",  0.3, 0, Double.MAX_VALUE);

    public static final ModConfigSpec.IntValue SCULK_WHISPER_COOLDOWN = BUILDER
            .comment( "Cooldown of sculk shrieker's InfrasoundBurst")
            .defineInRange("sculk_whisper_cooldown",  45, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SCULK_WHISPER_HURT_RANGE = BUILDER
            .comment( "Range of sculk shrieker's InfrasoundBurst hurt entity")
            .defineInRange("sculk_whisper_hurt_range",  8, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SCULK_WHISPER_AFFECT_RANGE = BUILDER
            .comment( "Range of sculk shrieker's InfrasoundBurst affect entity")
            .defineInRange("sculk_whisper_affect_range",  30, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SCULK_WHISPER_HURT_DAMAGE = BUILDER
            .comment( "Damage of sculk shrieker's InfrasoundBurst hurt entity")
            .defineInRange("sculk_whisper_hurt_damage",  15, 1, Integer.MAX_VALUE);


    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
    static final ModConfigSpec SPEC = BUILDER.build();
}