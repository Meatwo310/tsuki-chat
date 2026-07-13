package net.meatwo310.tsukichat;

import net.meatwo310.tsukichat.config.CommonConfigs;
import net.meatwo310.tsukichat.event.CommandRegisterer;
import net.meatwo310.tsukichat.event.PlayerCloneEvent;
import net.meatwo310.tsukichat.event.ServerChat;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Constants.MODID)
public class ModMain {
    public ModMain(IEventBus modEventBus, ModContainer modContainer) {
        Constants.LOGGER.debug(Constants.INITIALIZING, ModUtils.id("26.1.2-neo"));
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfigs.COMMON_SPEC);
        NeoForge.EVENT_BUS.addListener(CommandRegisterer::registerCommands);
        NeoForge.EVENT_BUS.addListener(PlayerCloneEvent::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ServerChat::onChat);
    }
}
