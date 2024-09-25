package io.github.meatwo310.tsukichat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class TsukiChatCommand {
    static final String PLACEHOLDER = "§e[TsukiChat]§r ";
    private static final SimpleCommandExceptionType ERROR_PERSONAL_SETTINGS_DISABLED = new SimpleCommandExceptionType(
            TsukiChatCommand.getErrorComponent("個人設定はサーバーによって無効化されています。")
    );

    public static Component getComponent(String ...message) {
        return Component.literal(PLACEHOLDER + String.join("", message));
    }

    public static Component getErrorComponent(String ...message) {
        return Component.literal(PLACEHOLDER + "§c" + String.join("", message));
    }

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
                ).then(Commands.literal("serverdict")
                        .then(Commands.literal("add")
                                .then(Commands.argument("key", StringArgumentType.string())
                                        .then(Commands.argument("value", StringArgumentType.string())
                                                .executes(ServerDictionaryCommand::add)))
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("key", StringArgumentType.string())
                                        .executes(ServerDictionaryCommand::remove))
                        )
                        .then(Commands.literal("removeall")
                                .then(Commands.argument("type_YES_if_you_are_sure", StringArgumentType.string())
                                        .executes(ServerDictionaryCommand::removeAll))
                        )
                        .then(Commands.literal("list")
                                .executes(ServerDictionaryCommand::list)
                        )
                ).then(Commands.literal("permission")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(Commands.literal("allow_personal_settings")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(PermissionCommand::allowPersonalSettings)))
                        .then(Commands.literal("allow_adding_server_dictionary")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(PermissionCommand::allowAddingServerDictionary)))
                        .then(Commands.literal("allow_removing_server_dictionary")
                                .then(Commands.argument("value", BoolArgumentType.bool())
                                        .executes(PermissionCommand::allowRemovingServerDictionary)))
                ).then(Commands.argument("arg", StringArgumentType.string())
                                .executes((CustomCommand::execute)))
        );
    }

    private enum ModeType {
        ENABLE,
        MARKDOWN,
        DISABLE,
        TOGGLE,
    }

    private static int mode(CommandContext<CommandSourceStack> ctx, ModeType modeType) throws CommandSyntaxException {
        Player player = (Player) ctx.getSource().getEntity();

        if (!(player instanceof Player)) return Command.SINGLE_SUCCESS;

        boolean allowPersonalSettings = CommonConfigs.allowPersonalSettings.get();
        if (!allowPersonalSettings) {
            throw ERROR_PERSONAL_SETTINGS_DISABLED.create();
        }

        Set<String> tags = player.getTags();
        String ignoreCompletelyTag = CommonConfigs.ignoreCompletelyTag.get();
        String ignoreTag = CommonConfigs.ignoreTag.get();

        StringBuilder message = new StringBuilder("個人設定を変更しました: ");
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

        ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(message.toString()), false);
        return Command.SINGLE_SUCCESS;
    }
}
