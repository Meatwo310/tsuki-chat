package net.meatwo310.tsukichat;

import net.meatwo310.tsukichat.compat.SDLinkServerChatEvent;
import net.meatwo310.tsukichat.compat.mohist.MohistHelper;
import net.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModMain.MODID)
public class ModMain {
    public static final String MODID = "tsukichat";

    public ModMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfigs.COMMON_SPEC);

        // sdlink compat
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            if (ModList.get().isLoaded("sdlink")) {
                Constants.LOGGER.info("Simple Discord Link is present!");
                Constants.LOGGER.info("Replacing the default chat event handler with Forge's event handler 😡");
                MinecraftForge.EVENT_BUS.register(new SDLinkServerChatEvent());
            }
        });

        // Mohist Compat
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            if (!MohistHelper.isMohistLoaded()) return;
            if (CommonConfigs.mohistCompat.get()) {
                Constants.LOGGER.warn("Mohist is present! Oh no!");
                Constants.LOGGER.warn("We will check if the compat plugin is loaded...");
            } else {
                Constants.LOGGER.warn("Mohist is present!");
                Constants.LOGGER.warn("Although the compat feature is disabled in the config so we will not do anything.");
            }
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}
