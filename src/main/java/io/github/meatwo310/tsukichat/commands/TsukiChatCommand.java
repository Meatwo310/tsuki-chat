package io.github.meatwo310.tsukichat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class TsukiChatCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tsukichat").executes(TsukiChatCommand::execute));
    }
    private static int execute(CommandContext<CommandSourceStack> command) {
        Player player = (Player) command.getSource().getEntity();

        if (!(player instanceof Player)) return Command.SINGLE_SUCCESS;

        boolean allowPersonalSettings = CommonConfigs.allowPersonalSettings.get();
        if (!allowPersonalSettings) {
            player.sendSystemMessage(Component.literal("§e[TsukiChat]§r 個人設定はサーバーによって無効化されています。"));
            return Command.SINGLE_SUCCESS;
        }

        Set<String> tags = player.getTags();
        String ignoreCompletelyTag = CommonConfigs.ignoreCompletelyTag.get();
        String ignoreTag = CommonConfigs.ignoreTag.get();

        StringBuilder message = new StringBuilder("§e[TsukiChat]§r 個人設定を変更しました: ");
        if (tags.contains(ignoreCompletelyTag)) {
            tags.remove(ignoreCompletelyTag);
            tags.remove(ignoreTag);
            message.append("§a有効§r");
        } else if (tags.contains(ignoreTag)) {
            tags.remove(ignoreTag);
            tags.add(ignoreCompletelyTag);
            message.append("§c無効§r");
        } else {
            tags.add(ignoreTag);
            message.append("§eMarkdownのみ§r");
        }
        player.sendSystemMessage(Component.literal(message.toString()));
        return Command.SINGLE_SUCCESS;
    }
}
