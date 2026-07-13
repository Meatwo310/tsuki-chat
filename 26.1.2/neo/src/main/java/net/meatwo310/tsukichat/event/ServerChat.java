package net.meatwo310.tsukichat.event;

import net.meatwo310.tsukichat.commands.ServerDictionaryCommand;
import net.meatwo310.tsukichat.commands.TsukiChatCommand;
import net.meatwo310.tsukichat.commands.UserDictionaryCommand;
import net.meatwo310.tsukichat.config.CommonConfigs;
import net.meatwo310.tsukichat.util.ChatCustomizer;
import net.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.neoforged.neoforge.event.ServerChatEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@ParametersAreNonnullByDefault
public class ServerChat {
    private static final Style TEAMMSG_SUGGEST_STYLE = Style.EMPTY
            .withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.type.team.hover")))
            .withClickEvent(new ClickEvent.SuggestCommand("/teammsg "));

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

        Set<String> playerTags = player.entityTags();

        CompoundTag dict = PlayerNbtUtil.loadCompoundTag(player, UserDictionaryCommand.KEY_NAME);
        LinkedHashMap<String, String> userDictionary = new LinkedHashMap<>();
        dict.keySet().forEach(k -> userDictionary.put(k, dict.getStringOr(k, "")));
        var serverDictionary = ServerDictionaryCommand.getServerDictionary();

        var result = ChatCustomizer.recognizeChat(original, playerTags, userDictionary, serverDictionary);

        result.ifMessagePresent(s -> event.setMessage(Component.literal(s)));
        result.ifDeferredMessagePresent(s ->
                player.level().getServer().getPlayerList().broadcastSystemMessage(Component.literal(s), false)
        );
    }

    private static void sendTeamMessage(ServerPlayer sender, String message) throws NotOnTeamException {
        PlayerTeam team = (PlayerTeam) sender.getTeam();
        if (team == null) throw new NotOnTeamException(sender);

        MinecraftServer server = sender.level().getServer();
        if (server == null) return;

        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        List<ServerPlayer> recipients = players.stream().filter(player ->
                player == sender || player.getTeam() == team
        ).toList();
        if (recipients.isEmpty()) return;

        Set<String> playerTags = sender.entityTags();

        CompoundTag dict = PlayerNbtUtil.loadCompoundTag(sender, UserDictionaryCommand.KEY_NAME);
        LinkedHashMap<String, String> userDictionary = new LinkedHashMap<>();
        dict.keySet().forEach(k -> userDictionary.put(k, dict.getStringOr(k, "")));
        var serverDictionary = ServerDictionaryCommand.getServerDictionary();

        var result = ChatCustomizer.recognizeChat(message, playerTags, userDictionary, serverDictionary);
        var converted = result.getMessageSynced();
        sendTeamMessage(server, sender, team, recipients, converted == null ? message : converted);
    }

    public static void sendTeamMessage(MinecraftServer server, ServerPlayer sender, PlayerTeam team, List<ServerPlayer> recipients, String message) {
        int forwardLevel = CommonConfigs.forwardTeamMsgLevel.get();
        ArrayList<ServerPlayer> listModified = new ArrayList<>(recipients);
        if (forwardLevel >= 0) listModified.addAll(server.getPlayerList().getPlayers().stream()
                .filter(player -> TsukiChatCommand.hasPermission(player, forwardLevel) && !listModified.contains(player))
                .toList()
        );

        var teamComponent = team.getFormattedDisplayName().withStyle(TEAMMSG_SUGGEST_STYLE);
        var senderComponent = sender.getDisplayName();
        var component = Component
                .literal("-> ")
                .append(teamComponent)
                .append(" <")
                .append(senderComponent)
                .append("> ")
                .append(message);

        for (ServerPlayer serverPlayer : listModified) {
            serverPlayer.sendSystemMessage(component);
        }
    }

    private static class NotOnTeamException extends RuntimeException {
        public NotOnTeamException(Entity entity) {
            super("Entity " + entity.getName().getString() + " is not on a team.");
        }
    }
}
