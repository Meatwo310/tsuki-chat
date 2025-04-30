package io.github.meatwo310.tsukichat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraft.commands.CommandSourceStack;

public class ConfigCommand {
    public static int defaultTeamMsg(CommandContext<CommandSourceStack> ctx) {
        boolean currentValue = CommonConfigs.defaultTeamMsg.get();
        try {
            boolean newValue = ctx.getArgument("value", Boolean.class);
            if (currentValue == newValue) {
                ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                        "チームメッセージはデフォルトで" + TsukiChatCommand.boolToStr(currentValue) + "のまま変更されませんでした"
                ), true);
            } else {
                CommonConfigs.defaultTeamMsg.set(newValue);
                ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                        "チームメッセージはデフォルトで" + TsukiChatCommand.boolToStr(newValue) + "に変更されました"
                ), true);
            }
        } catch (IllegalArgumentException e) {
            ctx.getSource().sendSuccess(() -> TsukiChatCommand.getComponent(
                    "チームメッセージはデフォルトで" + TsukiChatCommand.boolToStr(currentValue) + "です"
            ), false);
        }
        return Command.SINGLE_SUCCESS;
    }
}
