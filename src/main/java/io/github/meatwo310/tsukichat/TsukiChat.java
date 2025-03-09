package io.github.meatwo310.tsukichat;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.tsukichat.config.CommonConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(TsukiChat.MODID)
public class TsukiChat {
    public static final String MODID = "tsukichat";
    private static final Logger LOGGER = LogUtils.getLogger();

    public TsukiChat(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }
}
