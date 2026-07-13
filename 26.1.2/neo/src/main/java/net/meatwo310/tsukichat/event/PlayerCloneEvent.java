package net.meatwo310.tsukichat.event;

import net.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerCloneEvent {
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) PlayerNbtUtil.clonePlayerData(event.getOriginal(), event.getEntity());
    }
}
