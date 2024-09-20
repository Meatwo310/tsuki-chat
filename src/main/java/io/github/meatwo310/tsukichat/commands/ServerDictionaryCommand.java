package io.github.meatwo310.tsukichat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashMap;
import java.util.List;

public class ServerDictionaryCommand {
    public static final Component NOT_ENOUGH_PERMISSION_ADDING = Component.literal(TsukiChatCommand.PLACEHOLDER + "§cサーバー辞書に単語を追加する権限がありません。");
    public static final Component NOT_ENOUGH_PERMISSION_REMOVING = Component.literal(TsukiChatCommand.PLACEHOLDER + "§cサーバー辞書から単語を削除する権限がありません。");

    static int add(CommandContext<CommandSourceStack> ctx) {
        if (!checkPermission(ctx, PermissionActionType.ADD)) {
            ctx.getSource().sendFailure(NOT_ENOUGH_PERMISSION_ADDING);
            return 0;
        }

        LinkedHashMap<String, String> serverDictionary = getServerDictionary();
        String key = ctx.getArgument("key", String.class);
        String value = ctx.getArgument("value", String.class);

        if (serverDictionary.containsKey(key)) {
            ctx.getSource().sendSuccess(() -> Component.literal(TsukiChatCommand.PLACEHOLDER +
                    "サーバー辞書の単語を更新しました: " + key + " → " + value + " (元の値: " + serverDictionary.get(key) + ")"),
                    true
            );
        } else {
            ctx.getSource().sendSuccess(() -> Component.literal(TsukiChatCommand.PLACEHOLDER +
                    "サーバー辞書に単語を追加しました: " + key + " → " + value),
                    true
            );
        }
        serverDictionary.put(key, value);
        setServerDictionary(serverDictionary);

        return Command.SINGLE_SUCCESS;
    }

    static int remove(CommandContext<CommandSourceStack> ctx) {
        if (!checkPermission(ctx, PermissionActionType.REMOVE)) {
            ctx.getSource().sendFailure(NOT_ENOUGH_PERMISSION_REMOVING);
            return 0;
        }

        LinkedHashMap<String, String> serverDictionary = getServerDictionary();
        String key = ctx.getArgument("key", String.class);

        if (serverDictionary.containsKey(key)) {
            serverDictionary.remove(key);
            setServerDictionary(serverDictionary);
            ctx.getSource().sendSuccess(() -> Component.literal(TsukiChatCommand.PLACEHOLDER +
                    "サーバー辞書から単語を削除しました: " + key),
                    true
            );
        } else {
            ctx.getSource().sendFailure(Component.literal(TsukiChatCommand.PLACEHOLDER +
                    "§cサーバー辞書にキー " + key + " は存在しません。")
            );
        }

        return Command.SINGLE_SUCCESS;
    }

    static int removeAll(CommandContext<CommandSourceStack> ctx) {
        if (!checkPermission(ctx, PermissionActionType.REMOVE)) {
            ctx.getSource().sendFailure(NOT_ENOUGH_PERMISSION_REMOVING);
            return 0;
        }

        String confirm = ctx.getArgument("type_YES_if_you_are_sure", String.class);
        if (!confirm.equals("YES")) {
            ctx.getSource().sendFailure(Component.literal(TsukiChatCommand.PLACEHOLDER +
                    "§cユーザー辞書を全消去するには、 引数に§4YES§cを付加してください。"
            ));
            return 0;
        }

        LinkedHashMap<String, String> serverDictionary = getServerDictionary();
        serverDictionary.clear();
        setServerDictionary(serverDictionary);
        ctx.getSource().sendSuccess(() -> Component.literal(
                TsukiChatCommand.PLACEHOLDER + "サーバー辞書をクリアしました。"),
                true
        );

        return Command.SINGLE_SUCCESS;
    }

    static int list(CommandContext<CommandSourceStack> ctx) {
        LinkedHashMap<String, String> serverDictionary = getServerDictionary();
        if (serverDictionary.isEmpty()) {
            ctx.getSource().sendSuccess(() -> Component.literal(TsukiChatCommand.PLACEHOLDER +
                    "サーバー辞書は空です。"),
                    false
            );
            return Command.SINGLE_SUCCESS;
        }

        String contents = serverDictionary.entrySet().stream()
                .map(entry -> entry.getKey() + " → " + entry.getValue())
                .reduce((a, b) -> a + ",\n" + b)
                .orElse("");

        ctx.getSource().sendSuccess(() -> Component.literal(TsukiChatCommand.PLACEHOLDER +
                "サーバー辞書の内容:\n" + contents),
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
