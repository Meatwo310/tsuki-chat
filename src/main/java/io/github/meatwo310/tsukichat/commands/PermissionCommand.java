package io.github.meatwo310.tsukichat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraft.commands.CommandSourceStack;

public class PermissionCommand {
    public static int allowPersonalSettings(CommandContext<CommandSourceStack> ctx) {
        CommonConfigs.allowPersonalSettings.set(ctx.getArgument("value", Boolean.class));
        ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                        "個人設定の変更を", CommonConfigs.allowPersonalSettings.get() ? "許可" : "禁止", "しました。"),
                true
        );
        return Command.SINGLE_SUCCESS;
    }

    public static int allowAddingServerDictionary(CommandContext<CommandSourceStack> ctx) {
        CommonConfigs.allowAddingServerDictionary.set(ctx.getArgument("value", Boolean.class));
        ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                        "サーバー辞書への単語の追加を", CommonConfigs.allowAddingServerDictionary.get() ? "許可" : "禁止", "しました。"),
                true
        );
        return Command.SINGLE_SUCCESS;
    }

    public static int allowRemovingServerDictionary(CommandContext<CommandSourceStack> ctx) {
        CommonConfigs.allowRemovingServerDictionary.set(ctx.getArgument("value", Boolean.class));
        ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                        "サーバー辞書からの単語の削除を", CommonConfigs.allowRemovingServerDictionary.get() ? "許可" : "禁止", "しました。"),
                true
        );
        return Command.SINGLE_SUCCESS;
    }
}
