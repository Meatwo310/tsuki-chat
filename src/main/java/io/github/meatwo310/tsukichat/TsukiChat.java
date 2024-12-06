package io.github.meatwo310.tsukichat;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TsukiChat implements ModInitializer {
    public static final String MODID = "tsukichat";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        ForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.COMMON, CommonConfigs.COMMON_SPEC);
    }
}