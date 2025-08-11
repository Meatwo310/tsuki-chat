package io.github.meatwo310.tsukichat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraft.commands.CommandSourceStack;

import java.util.LinkedHashMap;
import java.util.List;

public class ServerDictionaryCommand {
    private static final SimpleCommandExceptionType ERROR_NO_PERMISSION_TO_ADD = new SimpleCommandExceptionType(
            TsukiChatCommand.getErrorComponent("サーバー辞書に単語を追加する権限がありません。")
    );
    private static final SimpleCommandExceptionType ERROR_NO_PERMISSION_TO_REMOVE = new SimpleCommandExceptionType(
            TsukiChatCommand.getErrorComponent("サーバー辞書から単語を削除する権限がありません。")
    );
    private static final DynamicCommandExceptionType ERROR_WORD_NOT_FOUND = new DynamicCommandExceptionType(key ->
            TsukiChatCommand.getErrorComponent("サーバー辞書にキー " + key + " は存在しません。")
    );
    private static final SimpleCommandExceptionType ERROR_NOT_CONFIRMED = new SimpleCommandExceptionType(
            TsukiChatCommand.getErrorComponent("サーバー辞書を全消去するには、 引数に§4YES§cを付加してください。")
    );

    static int add(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!checkPermission(ctx, PermissionActionType.ADD)) {
            throw ERROR_NO_PERMISSION_TO_ADD.create();
        }

        LinkedHashMap<String, String> serverDictionary = getServerDictionary();
        String key = ctx.getArgument("key", String.class);
        String value = ctx.getArgument("value", String.class);

        if (serverDictionary.containsKey(key)) {
            ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                            "サーバー辞書の単語を更新しました: ", key, " → ", value, " (元の値: ", serverDictionary.get(key), ")"),
                    true
            );
        } else {
            ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                            "サーバー辞書に単語を追加しました: ", key, " → ", value),
                    true
            );
        }
        serverDictionary.put(key, value);
        setServerDictionary(serverDictionary);

        return Command.SINGLE_SUCCESS;
    }

    static int remove(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!checkPermission(ctx, PermissionActionType.REMOVE)) {
            throw ERROR_NO_PERMISSION_TO_REMOVE.create();
        }

        LinkedHashMap<String, String> serverDictionary = getServerDictionary();
        String key = ctx.getArgument("key", String.class);

        if (serverDictionary.containsKey(key)) {
            serverDictionary.remove(key);
            setServerDictionary(serverDictionary);
            ctx.getSource().sendSuccess(
                    () -> TsukiChatCommand.getComponent(
                            "サーバー辞書から単語を削除しました: ", key),
                    true
            );
        } else {
            throw ERROR_WORD_NOT_FOUND.create(key);
        }

        return Command.SINGLE_SUCCESS;
    }

    static int removeAll(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        if (!checkPermission(ctx, PermissionActionType.REMOVE)) {
            throw ERROR_NO_PERMISSION_TO_REMOVE.create();
        }

        String confirm = ctx.getArgument("type_YES_if_you_are_sure", String.class);
        if (!confirm.equals("YES")) {
            throw ERROR_NOT_CONFIRMED.create();
        }

        LinkedHashMap<String, String> serverDictionary = getServerDictionary();
        serverDictionary.clear();
        setServerDictionary(serverDictionary);
        ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                        "サーバー辞書をクリアしました。"),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    static int list(CommandContext<CommandSourceStack> ctx) {
        LinkedHashMap<String, String> serverDictionary = getServerDictionary();
        if (serverDictionary.isEmpty()) {
            ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                            "サーバー辞書は空です。"),
                    false
            );
            return Command.SINGLE_SUCCESS;
        }

        String contents = serverDictionary.entrySet().stream()
                .map(entry -> entry.getKey() + " → " + entry.getValue())
                .reduce((a, b) -> a + ",\n" + b)
                .orElse("");

        ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                        "サーバー辞書の内容:\n", contents),
                false
        );

        return Command.SINGLE_SUCCESS;
    }

    public static LinkedHashMap<String, String> getServerDictionary() {
        List<? extends String> serverDictionary = CommonConfigs.serverDictionary.get();
        return serverDictionary.stream()
                .collect(LinkedHashMap::new, (map, entry) -> {
                    String[] keyValue = entry.split("\t", 2);
                    if (keyValue.length == 1) {
                        map.put(keyValue[0], "");
                    } else if (keyValue.length == 2) {
                        map.put(keyValue[0], keyValue[1]);
                    }
                }, LinkedHashMap::putAll);
    }

    public static void setServerDictionary(LinkedHashMap<String, String> serverDictionary) {
        CommonConfigs.serverDictionary.set(serverDictionary.entrySet().stream()
                .map(entry -> entry.getKey() + "\t" + entry.getValue())
                .toList());
    }

    private static boolean checkPermission(CommandContext<CommandSourceStack> ctx, PermissionActionType actionType) {
        if (ctx.getSource().hasPermission(2)) return true;
        return switch (actionType) {
            case ADD -> CommonConfigs.allowAddingServerDictionary.get();
            case REMOVE -> CommonConfigs.allowRemovingServerDictionary.get();
        };
    }

    private enum PermissionActionType {
        ADD,
        REMOVE
    }
}
