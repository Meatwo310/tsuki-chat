package net.meatwo310.tsukichat;

import net.meatwo310.tsukichat.config.ModConfigs;
import net.meatwo310.tsukichat.mdk.config.PlatformConfigRegistrar;
import net.meatwo310.tsukichat.mdk.config.VersionedConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MODID)
public class ModMain {
    public ModMain(FMLJavaModLoadingContext context) {
        Constants.LOGGER.debug(Constants.INITIALIZING, ModUtils.loc("1.19.2-forge"));
        PlatformConfigRegistrar.registerAll(context, VersionedConfigSpec.bindAll(ModConfigs.ALL));
    }
}
