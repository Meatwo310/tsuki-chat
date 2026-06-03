package net.meatwo310.tsukichat.event;

import net.meatwo310.tsukichat.commands.TsukiChatCommand;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class CommandRegisterer {
    public static void registerCommands(RegisterCommandsEvent event) {
        TsukiChatCommand.register(event.getDispatcher());
    }
}
