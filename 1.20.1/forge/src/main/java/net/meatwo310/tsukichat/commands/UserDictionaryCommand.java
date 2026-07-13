package net.meatwo310.tsukichat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.meatwo310.tsukichat.compat.mohist.MohistHelper;
import net.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class UserDictionaryCommand {
    public static final String KEY_NAME = "dict";
    
    private static final SimpleCommandExceptionType ERROR_NOT_IMPLEMENTED_ON_MOHIST = new SimpleCommandExceptionType(
            TsukiChatCommand.getErrorComponent("Mohist環境下では未実装です。")
    );
    private static final SimpleCommandExceptionType ERROR_NOT_CONFIRMED = new SimpleCommandExceptionType(
            TsukiChatCommand.getErrorComponent("ユーザー辞書を全消去するには、 引数に§cYES§rを付加してください。")
    );
    private static final SimpleCommandExceptionType ERROR_FAILED_TO_REMOVE = new SimpleCommandExceptionType(
            TsukiChatCommand.getErrorComponent("ユーザー辞書の削除に失敗しました。")
    );

    private static void checkMohist() throws CommandSyntaxException {
        if (MohistHelper.isMohistLoaded() && MohistHelper.isCompatPluginLoaded()) {
            throw ERROR_NOT_IMPLEMENTED_ON_MOHIST.create();
        } 
    }

    static int add(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!(ctx.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;
        checkMohist();

        String key = ctx.getArgument("key", String.class);
        String value = ctx.getArgument("value", String.class);

        CompoundTag entry = new CompoundTag();
        entry.putString(key, value);

        if (PlayerNbtUtil.saveCompoundTag(player, KEY_NAME, entry)) {
            ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                    "ユーザー辞書を更新しました: ", key, " → ", value
            ), false);
        } else {
            ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                    "ユーザー辞書を設定しました: ", key, " → ", value
            ), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    static int remove(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!(ctx.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;
        checkMohist();

        String key = ctx.getArgument("key", String.class);
        if (PlayerNbtUtil.removeTag(player, KEY_NAME, key)) {
            ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                    "ユーザー辞書から削除しました: ", key
            ), false);
        } else {
            ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                    "ユーザー辞書にキー ", key, " は存在しません。"
            ), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    static int removeAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!(ctx.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;
        checkMohist();

        String confirm = ctx.getArgument("type_YES_if_you_are_sure", String.class);

        CompoundTag tag = PlayerNbtUtil.loadCompoundTag(player, KEY_NAME);

        if (tag.isEmpty()) {
            ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                    "ユーザー辞書は既に空です。"
            ), false);
            return Command.SINGLE_SUCCESS;
        }

        if (!confirm.equals("YES")) {
            throw ERROR_NOT_CONFIRMED.create();
        }

        if (PlayerNbtUtil.removeWhole(player, KEY_NAME)) {
            ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                    "ユーザー辞書を全て削除しました。"
            ), false);
        } else {
            throw ERROR_FAILED_TO_REMOVE.create();
        }

        return Command.SINGLE_SUCCESS;
    }

    static int list(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!(ctx.getSource().getEntity() instanceof Player player)) return Command.SINGLE_SUCCESS;
        checkMohist();

        CompoundTag tag = PlayerNbtUtil.loadCompoundTag(player, KEY_NAME);

        if (tag.isEmpty()) {
            ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                    "ユーザー辞書は空です。"
            ), false);
            return Command.SINGLE_SUCCESS;
        }

        StringBuilder message = new StringBuilder();

        message.append("現在のユーザー辞書(");
        message.append(tag.getAllKeys().size());
        message.append("エントリ): ");

        tag.getAllKeys().forEach(k -> {
            message.append("\n");
            message.append(k);
            message.append(" → ");
            message.append(tag.getString(k));
        });

        ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(message.toString()), false);

        return Command.SINGLE_SUCCESS;
    }
}
