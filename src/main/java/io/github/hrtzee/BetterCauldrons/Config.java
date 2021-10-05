package io.github.hrtzee.BetterCauldrons;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.DoubleValue COOK_RATE;
    public static ForgeConfigSpec.BooleanValue DO_CONFUSION;

    static{
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("Cauldrons Settings").push("general");
        COOK_RATE = COMMON_BUILDER.comment("Cook Rate").defineInRange("cook_rate",1,0.0001,100);
        DO_CONFUSION = COMMON_BUILDER.comment("Do Player Confusion").define("do_confusion",true);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
