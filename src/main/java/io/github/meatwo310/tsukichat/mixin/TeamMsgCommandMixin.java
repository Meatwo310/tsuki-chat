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
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {
//    @Inject(at = @At("TAIL"), method = "sendMessage(" +
//            "Lnet/minecraft/commands/CommandSourceStack;" +
//            "Lnet/minecraft/world/entity/Entity;" +
//            "Lnet/minecraft/world/scores/PlayerTeam;" +
//            "Ljava/util/List;" +
//            "Lnet/minecraft/network/chat/PlayerChatMessage;" +
//            ")V",
//            locals = LocalCapture.CAPTURE_FAILHARD
//    )
//    private static void sendMessage(
//            CommandSourceStack source,
//            Entity senderEntity,
//            PlayerTeam team,
//            List<ServerPlayer> recipients,
//            PlayerChatMessage chatMessage,
//            CallbackInfo ci,
//            Component teamDisplayName,
//            ChatType.Bound incomingChatType,
//            ChatType.Bound outgoingChatType,
//            OutgoingChatMessage outgoingchatmessage
//    ) {
//        int forwardLevel = CommonConfigs.forwardTeamMsgLevel.get();
//        if (forwardLevel < 0) return;
//
//        source.getServer().getPlayerList().getPlayers().stream()
//                .filter(player -> player.hasPermissions(forwardLevel) && !recipients.contains(player))
//                .forEach(player -> player.sendChatMessage(outgoingchatmessage, false, outgoingChatType));
//    }

    //    @ModifyArgs(at = @At(
    //            value = "INVOKE",
    //            target = "Lnet/minecraft/server/commands/TeamMsgCommand;sendMessage(" +
    //                "Lnet/minecraft/commands/CommandSourceStack;" +
    //                "Lnet/minecraft/world/entity/Entity;" +
    //                "Lnet/minecraft/world/scores/PlayerTeam;" +
    //                "Ljava/util/List;" +
    //                "Lnet/minecraft/network/chat/PlayerChatMessage;" +
    //                ")V"),
    //            method = "lambda$register$1(" +
    //                    "Lnet/minecraft/commands/CommandSourceStack;" +
    //                    "Lnet/minecraft/world/entity/Entity;" +
    //                    "Lnet/minecraft/world/scores/PlayerTeam;" +
    //                    "Ljava/util/List;" +
    //                    "Lnet/minecraft/network/chat/PlayerChatMessage;" +
    //                    ")V"
    //    )
    //    private static void processTsukiChat(Args args) {
    ////        CommandSourceStack source;
    //        Entity senderEntity = args.get(1);
    ////        PlayerTeam team;
    ////        List<ServerPlayer> recipients;
    //        PlayerChatMessage chatMessage = args.get(4);
    //
    //        if (!CommonConfigs.formatTeamMsg.get()) return;
    //
    //        ServerPlayer sender = (ServerPlayer) senderEntity;
    //        Set<String> playerTags = sender.getTags();
    //
    //        CompoundTag dict = PlayerNbtUtil.loadCompoundTag(sender, UserDictionaryCommand.KEY_NAME);
    //        LinkedHashMap<String, String> userDictionary = new LinkedHashMap<>();
    //        dict.getAllKeys().forEach(k -> userDictionary.put(k, dict.getString(k)));
    //        var serverDictionary = ServerDictionaryCommand.getServerDictionary();
    //
    //        var result = ChatCustomizer.recognizeChat(chatMessage.signedContent(), playerTags, userDictionary, serverDictionary);
    //
    //        result.ifMessagePresent(converted -> tsukichat$setArg(args, chatMessage, converted));
    //        result.ifDefferedMessagePresentSync(converted -> tsukichat$setArg(args, chatMessage, converted));
    //    }
    //
    //    @Unique
    //    private static void tsukichat$setArg(Args args, PlayerChatMessage chatMessage, String converted) {
    //        args.set(4, PlayerChatMessage.unsigned(chatMessage.sender(), converted));
    //    }

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
