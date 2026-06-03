package net.meatwo310.tsukichat.event;

import net.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerCloneEvent {
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) PlayerNbtUtil.clonePlayerData(event.getOriginal(), event.getEntity());
    }
}
