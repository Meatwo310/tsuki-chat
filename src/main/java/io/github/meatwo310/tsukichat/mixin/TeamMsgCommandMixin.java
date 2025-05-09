package io.github.meatwo310.tsukichat.mixin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.meatwo310.tsukichat.commands.ServerDictionaryCommand;
import io.github.meatwo310.tsukichat.commands.UserDictionaryCommand;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import io.github.meatwo310.tsukichat.util.ChatCustomizer;
import io.github.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {
    @Inject(at = @At(
            value = "INVOKE",
            target = "net/minecraft/commands/arguments/MessageArgument.resolveChatMessage(" +
                    "Lcom/mojang/brigadier/context/CommandContext;" +
                    "Ljava/lang/String;" +
                    "Ljava/util/function/Consumer;" +
                    ")V"),
            method = "lambda$register$2(" +
                    "Lcom/mojang/brigadier/context/CommandContext;" +
                    ")I",
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void resolveChatMessage(
            CommandContext<CommandSourceStack> ctx,
            CallbackInfoReturnable<Integer> cir,
            CommandSourceStack commandsourcestack,
            Entity entity,
            PlayerTeam playerteam,
            List<ServerPlayer> list
    ) throws CommandSyntaxException {
        int forwardLevel = CommonConfigs.forwardTeamMsgLevel.get();
        ArrayList<ServerPlayer> listModified = new ArrayList<>(list);
        if (forwardLevel >= 0) listModified.addAll(commandsourcestack.getServer().getPlayerList().getPlayers().stream()
                    .filter(player -> player.hasPermissions(forwardLevel) && !listModified.contains(player))
                    .toList()
        );

        MessageArgument.resolveChatMessage(ctx, "message", (playerChatMessage -> {
            PlayerChatMessage processed = tsukichat$processTsukiChat(playerChatMessage, (ServerPlayer) entity);
            TeamMsgCommand.sendMessage(commandsourcestack, entity, playerteam, list, processed);
        }));
    }

    @Unique
    private static PlayerChatMessage tsukichat$processTsukiChat(PlayerChatMessage playerChatMessage, ServerPlayer player) {
        var original = playerChatMessage.signedContent();
        Set<String> playerTags = player.getTags();

        CompoundTag dict = PlayerNbtUtil.loadCompoundTag(player, UserDictionaryCommand.KEY_NAME);
        LinkedHashMap<String, String> userDictionary = new LinkedHashMap<>();
        dict.getAllKeys().forEach(k -> userDictionary.put(k, dict.getString(k)));
        var serverDictionary = ServerDictionaryCommand.getServerDictionary();

        var result = ChatCustomizer.recognizeChat(original, playerTags, userDictionary, serverDictionary);

        String converted = result.getMessageSynced();
        if (converted == null) return playerChatMessage;

        return PlayerChatMessage.unsigned(playerChatMessage.sender(), converted);
    }
}
