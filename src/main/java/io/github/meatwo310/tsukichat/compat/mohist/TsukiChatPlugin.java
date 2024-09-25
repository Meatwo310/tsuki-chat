package io.github.meatwo310.tsukichat.compat.mohist;

import io.github.meatwo310.tsukichat.commands.ServerDictionaryCommand;
import io.github.meatwo310.tsukichat.util.ChatCustomizer;
import io.github.meatwo310.tsukichat.util.CustomizedChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Set;

public final class TsukiChatPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        MohistHelper.compatPluginLoaded = true;
        System.out.println("Enabled TsukiChat Mohist Compat Plugin");
    }

    @Override
    public void onDisable() {
        MohistHelper.compatPluginLoaded = false;
        System.out.println("Disabled TsukiChat Mohist Compat Plugin");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!MohistHelper.isMohistCompatEnabled()) return;

        Player player = event.getPlayer();
        String original = event.getMessage();
        Set<String> playerTags = player.getScoreboardTags();

        // TODO: implement user dictionary on Spigot
        LinkedHashMap<String, String> userDictionary = new LinkedHashMap<>();
        LinkedHashMap<String, String> serverDictionary = ServerDictionaryCommand.getServerDictionary();

        CustomizedChat result = ChatCustomizer.recognizeChat(original, playerTags, userDictionary, serverDictionary);

        result.ifMessagePresent(event::setMessage);
        result.ifDeferredMessagePresent(s -> player.getServer().broadcastMessage(s));
    }
}
