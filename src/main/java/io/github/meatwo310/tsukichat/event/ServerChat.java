package io.github.meatwo310.tsukichat.event;

import io.github.meatwo310.tsukichat.TsukiChat;
import io.github.meatwo310.tsukichat.commands.ServerDictionaryCommand;
import io.github.meatwo310.tsukichat.commands.UserDictionaryCommand;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import io.github.meatwo310.tsukichat.util.ChatCustomizer;
import io.github.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = TsukiChat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerChat {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player.level().isClientSide()) return;

        String original = event.getRawText();

        if (CommonConfigs.defaultTeamMsg.get()) {
            if (CommonConfigs.forceGlobal.get().stream().anyMatch(original::startsWith)) {
                original = original.substring(1);
            } else {
                try {
                    sendTeamMessage(event.getPlayer(), original);
                    event.setCanceled(true);
                    return;
                } catch (NotOnTeamException ignored) {
                    // Continue to process the message as a normal chat message
                }
            }
        }

        Set<String> playerTags = player.getTags();

        CompoundTag dict = PlayerNbtUtil.loadCompoundTag(player, UserDictionaryCommand.KEY_NAME);
        LinkedHashMap<String, String> userDictionary = new LinkedHashMap<>();
        dict.getAllKeys().forEach(k -> userDictionary.put(k, dict.getString(k)));
        var serverDictionary = ServerDictionaryCommand.getServerDictionary();

        var result = ChatCustomizer.recognizeChat(original, playerTags, userDictionary, serverDictionary);

        result.ifMessagePresent(s -> event.setMessage(Component.literal(s)));
        result.ifDeferredMessagePresent(s ->
                player.server.getPlayerList().broadcastSystemMessage(Component.literal(s), false)
        );
    }

    private static void sendTeamMessage(ServerPlayer sender, String message) throws NotOnTeamException {
        PlayerTeam team = (PlayerTeam) sender.getTeam();
        if (team == null) throw new NotOnTeamException(sender);

        MinecraftServer server = sender.getServer();
        if (server == null) return;

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        List<ServerPlayer> recipients = CommonConfigs.defaultTeamMsg.get() ? players.stream().filter(player ->
                player == sender || player.getTeam() == team || player.hasPermissions(CommonConfigs.forwardTeamMsgLevel.get())
        ).toList() : players.stream().filter(player ->
                player == sender || player.getTeam() == team
        ).toList();
        if (recipients.isEmpty()) return;

        var chatMessage = PlayerChatMessage.unsigned(sender.getUUID(), message);
        sendTeamMessage(sender, team, recipients, chatMessage);
    }


    private static void sendTeamMessage(ServerPlayer sender, PlayerTeam team, List<ServerPlayer> recipients, PlayerChatMessage chatMessage) {
        Component teamDisplayName = team.getFormattedDisplayName();
        Component senderName = sender.getDisplayName();
        RegistryAccess.Frozen registryAccess = sender.server.registryAccess();
        ChatType.Bound incomingChatType = ChatType.bind(ChatType.TEAM_MSG_COMMAND_INCOMING, registryAccess, senderName).withTargetName(teamDisplayName);
        ChatType.Bound outgoingChatType = ChatType.bind(ChatType.TEAM_MSG_COMMAND_OUTGOING, registryAccess, senderName).withTargetName(teamDisplayName);
        OutgoingChatMessage outgoingMessage = OutgoingChatMessage.create(chatMessage);
        boolean anyMessageFullyFiltered = false;

        for (ServerPlayer recipient : recipients) {
            var chatType = recipient == sender ? outgoingChatType : incomingChatType;
            boolean shouldFilter = shouldFilterMessage(sender, recipient);
            recipient.sendChatMessage(outgoingMessage, shouldFilter, chatType);
            anyMessageFullyFiltered |= shouldFilter && chatMessage.isFullyFiltered();
        }

        if (anyMessageFullyFiltered) {
            sender.sendSystemMessage(PlayerList.CHAT_FILTERED_FULL);
        }
    }

    private static boolean shouldFilterMessage(ServerPlayer sender, ServerPlayer recipient) {
        if (sender == recipient) return false;
        return sender != null && sender.isTextFilteringEnabled() || recipient.isTextFilteringEnabled();
    }

    private static class NotOnTeamException extends RuntimeException {
        public NotOnTeamException(Entity entity) {
            super("Entity " + entity.getName().getString() + " is not on a team.");
        }
    }
}
