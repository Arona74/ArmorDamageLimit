package com.rinko1231.armordamagelimit.config;

import com.rinko1231.armordamagelimit.ArmorDamageLimit;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;

public class ArmorProtectionConfig
{
    public static ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    // Tiered damage system configuration
    public static ForgeConfigSpec.IntValue tier1Threshold;
    public static ForgeConfigSpec.IntValue tier2Threshold;
    public static ForgeConfigSpec.IntValue tier1Damage;
    public static ForgeConfigSpec.IntValue tier2Damage;
    public static ForgeConfigSpec.IntValue tier3Damage;
    
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> itemProtectionBlacklist;

    static
    {
        BUILDER.push("Config");
        
        tier1Threshold = BUILDER
                .comment("Maximum damage for Tier 1 (lowest damage tier)",
                         "Damage amounts <= this value will cause Tier 1 durability loss")
                .defineInRange("Tier 1 Threshold", 25, 1, 1000);
        
        tier2Threshold = BUILDER
                .comment("Maximum damage for Tier 2 (middle damage tier)",
                         "Damage amounts > Tier 1 but <= this value will cause Tier 2 durability loss")
                .defineInRange("Tier 2 Threshold", 50, 1, 1000);
        
        tier1Damage = BUILDER
                .comment("Durability damage for Tier 1 hits")
                .defineInRange("Tier 1 Durability Damage", 1, 1, 20);
        
        tier2Damage = BUILDER
                .comment("Durability damage for Tier 2 hits")  
                .defineInRange("Tier 2 Durability Damage", 2, 1, 20);
        
        tier3Damage = BUILDER
                .comment("Durability damage for Tier 3 hits (above Tier 2 threshold)")
                .defineInRange("Tier 3 Durability Damage", 3, 1, 20);
        
        itemProtectionBlacklist = BUILDER
                .comment("Armor items that will not be protected by the tiered system")
                .defineList("Item Protection Blacklist", List.of("modA:armorB"),
                        element -> element instanceof String);
        
        SPEC = BUILDER.build();
    }

    public static void setup()
    {
        ForgeConfigRegistry.INSTANCE.register(ArmorDamageLimit.MOD_ID, ModConfig.Type.COMMON, SPEC);
    }
}