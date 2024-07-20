package io.github.meatwo310.tsukichat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.github.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

public class UserDictionaryCommand {
    public static final String KEY_NAME = "dict";

    static int add(CommandContext<CommandSourceStack> command) {
        if (!(command.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;

        String key = command.getArgument("key", String.class);
        String value = command.getArgument("value", String.class);

        CompoundTag entry = new CompoundTag();
        entry.putString(key, value);

        if (PlayerNbtUtil.saveCompoundTag(player, KEY_NAME, entry)) {
            player.sendMessage(
                    new TextComponent(TsukiChatCommand.PLACEHOLDER + "ユーザー辞書を更新しました: " +
                            key + " → " + value),
                    Util.NIL_UUID
            );
        } else {
            player.sendMessage(
                    new TextComponent(TsukiChatCommand.PLACEHOLDER + "ユーザー辞書を設定しました: " +
                            key + " → " + value),
                    Util.NIL_UUID
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    static int remove(CommandContext<CommandSourceStack> command) {
        if (!(command.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;

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
        player.sendMessage(new TextComponent(message.toString()), Util.NIL_UUID);

        return Command.SINGLE_SUCCESS;
    }

    public static int removeAll(CommandContext<CommandSourceStack> command) {
        if (!(command.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;

        String confirm = command.getArgument("type_YES_if_you_are_sure", String.class);

        CompoundTag tag = PlayerNbtUtil.loadCompoundTag(player, KEY_NAME);

        if (tag.isEmpty()) {
            player.sendMessage(
                    new TextComponent(TsukiChatCommand.PLACEHOLDER + "ユーザー辞書は既に空です。"),
                    Util.NIL_UUID
            );
            return Command.SINGLE_SUCCESS;
        }

        if (!confirm.equals("YES")) {
            player.sendMessage(
                    new TextComponent(TsukiChatCommand.PLACEHOLDER +
                            "ユーザー辞書を全消去するには、 引数に§cYES§rを付加してください。"),
                    Util.NIL_UUID
            );
            return Command.SINGLE_SUCCESS;
        }

        if (PlayerNbtUtil.removeWhole(player, KEY_NAME)) {
            player.sendMessage(
                    new TextComponent(TsukiChatCommand.PLACEHOLDER + "ユーザー辞書を全て削除しました。"),
                    Util.NIL_UUID
            );
        } else {
            player.sendMessage(
                    new TextComponent(TsukiChatCommand.PLACEHOLDER + "ユーザー辞書の削除に失敗しました。"),
                    Util.NIL_UUID
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    static int list(CommandContext<CommandSourceStack> command) {
        if (!(command.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;

        CompoundTag tag = PlayerNbtUtil.loadCompoundTag(player, KEY_NAME);

        if (tag.isEmpty()) {
            player.sendMessage(
                    new TextComponent(TsukiChatCommand.PLACEHOLDER + "ユーザー辞書は空です。"),
                    Util.NIL_UUID
            );
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

        player.sendMessage(new TextComponent(message.toString()), Util.NIL_UUID);

        return Command.SINGLE_SUCCESS;
    }
}
