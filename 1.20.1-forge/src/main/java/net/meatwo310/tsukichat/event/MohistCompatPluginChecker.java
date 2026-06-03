package net.meatwo310.tsukichat.event;

import net.meatwo310.tsukichat.ModMain;
import net.meatwo310.tsukichat.compat.mohist.MohistHelper;
import net.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class MohistCompatPluginChecker {
    @SubscribeEvent
    public static void check(ServerStartedEvent event) {
        if (!MohistHelper.isMohistLoaded()) return;
        if (!CommonConfigs.mohistCompat.get()) return;

        if (MohistHelper.isCompatPluginLoaded()) {
            ModMain.LOGGER.info("Mohist compat plugin is loaded! Proceeding...");
        } else {
            throw new IllegalStateException("Forge's ServerChatEvent is NOT compatible with Mohist! " +
                    "Install the compat plugin at https://github.com/Meatwo310/tsuki-chat/blob/main/mohist-compat.md ! " +
                    "Note that you can disable this check in the config.");
        }
    }
}
