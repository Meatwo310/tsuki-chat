package io.github.meatwo310.tsukichat.event;

import io.github.meatwo310.tsukichat.commands.ServerDictionaryCommand;
import io.github.meatwo310.tsukichat.commands.UserDictionaryCommand;
import io.github.meatwo310.tsukichat.util.ChatCustomizer;
import io.github.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.LinkedHashMap;
import java.util.Set;

@Mod.EventBusSubscriber
public class ServerChat {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player.level().isClientSide()) return;

        String original = event.getRawText();
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
}
