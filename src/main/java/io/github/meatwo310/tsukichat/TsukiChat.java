package io.github.meatwo310.tsukichat;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.tsukichat.compat.SDLinkServerChatEvent;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
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
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TsukiChat.MODID)
public class TsukiChat {
    public static final String MODID = "tsukichat";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TsukiChat() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfigs.COMMON_SPEC);

        // sdlink compat
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            if (ModList.get().isLoaded("sdlink")) {
                LOGGER.info("Simple Discord Link is present!");
                LOGGER.info("Replacing the default chat event handler with Forge's event handler 😡");
                MinecraftForge.EVENT_BUS.register(new SDLinkServerChatEvent());
            }
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
}
