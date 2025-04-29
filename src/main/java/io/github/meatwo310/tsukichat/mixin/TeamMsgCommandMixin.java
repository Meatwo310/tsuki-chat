package io.github.meatwo310.tsukichat.mixin;

import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {
    @Inject(at = @At("TAIL"), method = "sendMessage(" +
            "Lnet/minecraft/commands/CommandSourceStack;" +
            "Lnet/minecraft/world/entity/Entity;" +
            "Lnet/minecraft/world/scores/PlayerTeam;" +
            "Ljava/util/List;" +
            "Lnet/minecraft/network/chat/PlayerChatMessage;" +
            ")V",
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void sendMessage(
            CommandSourceStack source,
            Entity senderEntity,
            PlayerTeam team,
            List<ServerPlayer> recipients,
            PlayerChatMessage chatMessage,
            CallbackInfo ci,
            Component teamDisplayName,
            ChatType.Bound incomingChatType,
            ChatType.Bound outgoingChatType,
            OutgoingChatMessage outgoingchatmessage
    ) {
        int forwardLevel = CommonConfigs.forwardTeamMsgLevel.get();
        if (forwardLevel < 0) return;

        source.getServer().getPlayerList().getPlayers().stream()
                .filter(player -> player.hasPermissions(forwardLevel) && !recipients.contains(player))
                .forEach(player -> player.sendChatMessage(outgoingchatmessage, false, outgoingChatType));
    }
}
