package io.github.meatwo310.tsukichat.mixin;

import com.hypherionmc.craterlib.api.events.server.CraterServerChatEvent;
import com.hypherionmc.sdlink.server.ServerEvents;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerEvents.class)
public class SDLinkServerEventsMixin {
    @Inject(
            method = "onServerChatEvent(Lcom/hypherionmc/craterlib/api/events/server/CraterServerChatEvent;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false // ← 難読化されてない場合falseにしないとエラー(1敗)
    )
    private void onServerChatEvent(CraterServerChatEvent event, CallbackInfo ci) {
        if (!CommonConfigs.sdlinkCompat.get()) return;
        ci.cancel();
    }
}
