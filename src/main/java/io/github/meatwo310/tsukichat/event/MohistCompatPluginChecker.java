package io.github.meatwo310.tsukichat.event;

import io.github.meatwo310.tsukichat.TsukiChat;
import io.github.meatwo310.tsukichat.compat.mohist.MohistHelper;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = TsukiChat.MODID, value = Dist.DEDICATED_SERVER)
public class MohistCompatPluginChecker {
    @SubscribeEvent
    public static void check(ServerStartedEvent event) {
        if (!MohistHelper.isMohistLoaded()) return;
        if (!CommonConfigs.mohistCompat.get()) return;

        if (MohistHelper.isCompatPluginLoaded()) {
            TsukiChat.LOGGER.info("Mohist compat plugin is loaded! Proceeding...");
        } else {
            throw new IllegalStateException("Forge's ServerChatEvent is NOT compatible with Mohist! " +
                    "Install the compat plugin at https://github.com/Meatwo310/tsuki-chat/blob/main/mohist-compat.md ! " +
                    "Note that you can disable this check in the config.");
        }
    }
}
