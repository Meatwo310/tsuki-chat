package io.github.meatwo310.tsukichat;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfigs.COMMON_SPEC);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }
}
