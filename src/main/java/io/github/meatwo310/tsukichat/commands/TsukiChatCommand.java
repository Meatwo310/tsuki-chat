package io.github.meatwo310.tsukichat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class TsukiChatCommand {
    static final String PLACEHOLDER = "§e[TsukiChat]§r ";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tsukichat")
                .then(Commands.literal("mode")
                        .then(Commands.literal("enable")
                                .executes(commandContext -> mode(commandContext, ModeType.ENABLE)))
                        .then(Commands.literal("markdown")
                                .executes(commandContext -> mode(commandContext, ModeType.MARKDOWN)))
                        .then(Commands.literal("disable")
                                .executes(commandContext -> mode(commandContext, ModeType.DISABLE)))
                        .then(Commands.literal("toggle")
                                .executes(commandContext -> mode(commandContext, ModeType.TOGGLE)))
                        .executes(commandContext -> mode(commandContext, ModeType.TOGGLE))
                )
                .then(Commands.literal("userdict")
                        .then(Commands.literal("add")
                                .then(Commands.argument("key", StringArgumentType.string())
                                        .then(Commands.argument("value", StringArgumentType.string())
                                                .executes(UserDictionaryCommand::add)))
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("key", StringArgumentType.string())
                                        .executes(UserDictionaryCommand::remove))
                        )
                        .then(Commands.literal("removeall")
                                .then(Commands.argument("type_YES_if_you_are_sure", StringArgumentType.string())
                                        .executes(UserDictionaryCommand::removeAll))
                        )
                        .then(Commands.literal("list")
                                .executes(UserDictionaryCommand::list)
                        )
                )
        );
    }

    private enum ModeType {
        ENABLE,
        MARKDOWN,
        DISABLE,
        TOGGLE,
    }

    private static int mode(CommandContext<CommandSourceStack> command, ModeType modeType) {
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

        StringBuilder message = new StringBuilder(PLACEHOLDER).append("個人設定を変更しました: ");
        switch (modeType) {
            case ENABLE -> {
                tags.remove(ignoreCompletelyTag);
                tags.remove(ignoreTag);
                message.append("§a有効§r");
            }
            case MARKDOWN -> {
                tags.remove(ignoreCompletelyTag);
                tags.add(ignoreTag);
                message.append("§eMarkdownのみ§r");
            }
            case DISABLE -> {
                tags.remove(ignoreTag);
                tags.add(ignoreCompletelyTag);
                message.append("§c無効§r");
            }
            case TOGGLE -> {
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
            }
        }

        player.sendSystemMessage(Component.literal(message.toString()));
        return Command.SINGLE_SUCCESS;
    }
}
