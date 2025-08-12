package io.github.meatwo310.tsukichat.compat;

import com.hypherionmc.craterlib.nojang.world.entity.player.BridgedPlayer;
import com.hypherionmc.sdlink.core.managers.HiddenPlayersManager;
import com.hypherionmc.sdlink.platform.SDLinkMCPlatform;
import com.hypherionmc.sdlink.server.ServerEvents;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import shadow.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.mojang.serialization.JsonOps;

public class SDLinkServerChatEvent {
    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        if (!CommonConfigs.sdlinkCompat.get()) return;

        BridgedPlayer player = BridgedPlayer.of(event.getPlayer());
        String messageString = Component.Serializer.toJson(event.getMessage(), (HolderLookup.Provider) JsonOps.INSTANCE);
        shadow.kyori.adventure.text.Component adventureComponent = GsonComponentSerializer.gson().deserialize(messageString);

        if (SDLinkMCPlatform.INSTANCE.playerIsActive(player)) {
            if (!HiddenPlayersManager.INSTANCE.isPlayerHidden(event.getPlayer().getStringUUID())) {
                ServerEvents.getInstance().onServerChatEvent(
                        adventureComponent,
                        player.getDisplayName(),
                        SDLinkMCPlatform.INSTANCE.getPlayerSkinUUID(player),
                        player.getGameProfile(),
                        false);
            }
        }
    }
}
