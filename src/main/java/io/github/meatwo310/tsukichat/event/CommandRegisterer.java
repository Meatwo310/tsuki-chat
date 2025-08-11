package io.github.meatwo310.tsukichat.event;

import io.github.meatwo310.tsukichat.commands.TsukiChatCommand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class CommandRegisterer {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        TsukiChatCommand.register(event.getDispatcher());
    }
}
