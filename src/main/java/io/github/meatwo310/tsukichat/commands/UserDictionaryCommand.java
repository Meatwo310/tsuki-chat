package io.github.meatwo310.tsukichat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.github.meatwo310.tsukichat.compat.mohist.MohistHelper;
import io.github.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class UserDictionaryCommand {
    public static final String KEY_NAME = "dict";

    private static boolean checkMohist(CommandContext<CommandSourceStack> ctx) {
        if (MohistHelper.isMohistLoaded() && MohistHelper.isCompatPluginLoaded()) {
            ctx.getSource().sendFailure(Component.literal(
                    TsukiChatCommand.PLACEHOLDER + "Mohist環境下では未実装です。"
            ));
            return true;
        } else {
            return false;
        }
    }

    static int add(CommandContext<CommandSourceStack> command) {
        if (!(command.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;
        if (checkMohist(command)) return 0;

        String key = command.getArgument("key", String.class);
        String value = command.getArgument("value", String.class);

        CompoundTag entry = new CompoundTag();
        entry.putString(key, value);

        if (PlayerNbtUtil.saveCompoundTag(player, KEY_NAME, entry)) {
            player.sendSystemMessage(Component.literal(
                    TsukiChatCommand.PLACEHOLDER + "ユーザー辞書を更新しました: " +
                    key + " → " + value
            ));
        } else {
            player.sendSystemMessage(Component.literal(
                    TsukiChatCommand.PLACEHOLDER + "ユーザー辞書を設定しました: " +
                    key + " → " + value
            ));
        }

        return Command.SINGLE_SUCCESS;
    }

    static int remove(CommandContext<CommandSourceStack> command) {
        if (!(command.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;
        if (checkMohist(command)) return 0;

        String key = command.getArgument("key", String.class);
        StringBuilder message = new StringBuilder(TsukiChatCommand.PLACEHOLDER);
        if (PlayerNbtUtil.removeTag(player, KEY_NAME, key)) {
            message.append("ユーザー辞書から削除しました: ");
            message.append(key);
        } else {
            message.append("ユーザー辞書にキー ");
            message.append(key);
            message.append(" は存在しません。");
        }
        player.sendSystemMessage(Component.literal(message.toString()));

        return Command.SINGLE_SUCCESS;
    }

    public static int removeAll(CommandContext<CommandSourceStack> command) {
        if (!(command.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;
        if (checkMohist(command)) return 0;

        String confirm = command.getArgument("type_YES_if_you_are_sure", String.class);

        CompoundTag tag = PlayerNbtUtil.loadCompoundTag(player, KEY_NAME);

        if (tag.isEmpty()) {
            player.sendSystemMessage(Component.literal(
                    TsukiChatCommand.PLACEHOLDER + "ユーザー辞書は既に空です。"
            ));
            return Command.SINGLE_SUCCESS;
        }

        if (!confirm.equals("YES")) {
            player.sendSystemMessage(Component.literal(
                    TsukiChatCommand.PLACEHOLDER +
                            "ユーザー辞書を全消去するには、 引数に§cYES§rを付加してください。"
            ));
            return Command.SINGLE_SUCCESS;
        }

        if (PlayerNbtUtil.removeWhole(player, KEY_NAME)) {
            player.sendSystemMessage(Component.literal(
                    TsukiChatCommand.PLACEHOLDER + "ユーザー辞書を全て削除しました。"
            ));
        } else {
            player.sendSystemMessage(Component.literal(
                    TsukiChatCommand.PLACEHOLDER + "ユーザー辞書の削除に失敗しました。"
            ));
        }

        return Command.SINGLE_SUCCESS;
    }

    static int list(CommandContext<CommandSourceStack> command) {
        if (!(command.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;
        if (checkMohist(command)) return 0;

        CompoundTag tag = PlayerNbtUtil.loadCompoundTag(player, KEY_NAME);

        if (tag.isEmpty()) {
            player.sendSystemMessage(Component.literal(
                    TsukiChatCommand.PLACEHOLDER + "ユーザー辞書は空です。"
            ));
            return Command.SINGLE_SUCCESS;
        }

        StringBuilder message = new StringBuilder(TsukiChatCommand.PLACEHOLDER);

        message.append("現在のユーザー辞書(");
        message.append(tag.getAllKeys().size());
        message.append("エントリ): ");

        tag.getAllKeys().forEach(k -> {
            message.append("\n");
            message.append(k);
            message.append(" → ");
            message.append(tag.getString(k));
        });

        player.sendSystemMessage(Component.literal(message.toString()));

        return Command.SINGLE_SUCCESS;
    }
}
