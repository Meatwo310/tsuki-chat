package io.github.meatwo310.tsukichat.event;

import io.github.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class PlayerCloneEvent {
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) PlayerNbtUtil.clonePlayerData(event.getOriginal(), event.getEntity());
    }
}
