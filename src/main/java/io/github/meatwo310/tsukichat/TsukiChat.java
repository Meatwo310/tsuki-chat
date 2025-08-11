package io.github.meatwo310.tsukichat;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.tsukichat.compat.SDLinkServerChatEvent;
import io.github.meatwo310.tsukichat.compat.mohist.MohistHelper;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TsukiChat.MODID)
public class TsukiChat {
    public static final String MODID = "tsukichat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TsukiChat(IEventBus modEventBus) {
        modEventBus.addListener(this::commonSetup);
        ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.COMMON, CommonConfigs.COMMON_SPEC, "tsukichat-common.toml");

        // sdlink compat
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            if (ModList.get().isLoaded("sdlink")) {
                LOGGER.info("Simple Discord Link is present!");
                LOGGER.info("Replacing the default chat event handler with Forge's event handler 😡");
                NeoForge.EVENT_BUS.register(new SDLinkServerChatEvent());
            }
        }

        // Mohist Compat
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            if (!MohistHelper.isMohistLoaded()) return;
            if (CommonConfigs.mohistCompat.get()) {
                LOGGER.warn("Mohist is present! Oh no!");
                LOGGER.warn("We will check if the compat plugin is loaded...");
            } else {
                LOGGER.warn("Mohist is present!");
                LOGGER.warn("Although the compat feature is disabled in the config so we will not do anything.");
            }
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}
