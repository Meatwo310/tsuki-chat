package net.meatwo310.tsukichat;

import net.fabricmc.api.ModInitializer;
import net.meatwo310.tsukichat.config.ModConfigs;
import net.meatwo310.tsukichat.mdk.config.PlatformConfigRegistrar;
import net.meatwo310.tsukichat.mdk.config.VersionedConfigSpec;

public class ModMain implements ModInitializer {
    @Override
    public void onInitialize() {
        Constants.LOGGER.debug(Constants.INITIALIZING, ModUtils.id("26.1-fabric"));
        PlatformConfigRegistrar.registerAll(Constants.MODID, VersionedConfigSpec.bindAll(ModConfigs.ALL));
    }
}
