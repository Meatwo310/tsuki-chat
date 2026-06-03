package net.meatwo310.tsukichat.event;

import net.meatwo310.tsukichat.commands.TsukiChatCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandRegisterer {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        TsukiChatCommand.register(event.getDispatcher());
    }
}
