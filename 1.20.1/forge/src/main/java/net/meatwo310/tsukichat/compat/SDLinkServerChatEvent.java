package net.meatwo310.tsukichat.compat;

import com.hypherionmc.craterlib.api.game.text.Text;
import com.hypherionmc.craterlib.impl.api.world.entity.player.BridgedPlayer;
import com.hypherionmc.sdlink.core.managers.HiddenPlayersManager;
import com.hypherionmc.sdlink.platform.SDLinkMCPlatform;
import com.hypherionmc.sdlink.server.ServerEvents;
import net.meatwo310.tsukichat.config.CommonConfigs;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SDLinkServerChatEvent {
    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        if (!CommonConfigs.sdlinkCompat.get()) return;

        BridgedPlayer player = BridgedPlayer.wrap(event.getPlayer());
        Text message = Text.fromGame(event.getMessage());

        if (SDLinkMCPlatform.INSTANCE.playerIsActive(player)) {
            if (!HiddenPlayersManager.INSTANCE.isPlayerHidden(event.getPlayer().getStringUUID())) {
                ServerEvents.getInstance().onServerChatEvent(
                        message,
                        player.getDisplayName(),
                        SDLinkMCPlatform.INSTANCE.getPlayerSkinUUID(player),
                        player.getGameProfile(),
                        false);
            }
        }
    }
}
