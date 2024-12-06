package io.github.meatwo310.tsukichat.mixin;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.tsukichat.util.ChatCustomizer;
import io.github.meatwo310.tsukichat.util.CustomizedChat;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.LinkedHashMap;
import java.util.Set;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;
    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    @ModifyVariable(method = "handleDecoratedMessage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private SignedMessage modifySignedMessage(SignedMessage message) {
        ServerPlayerEntity player = this.player;
        if (player.getServerWorld().isClient()) return message;

        String original = message.getContent().getString();
        LOGGER.info("handleDecoratedMessage called: {}", original);

        CustomizedChat result = ChatCustomizer.recognizeChat(original, Set.of(), new LinkedHashMap<>(), new LinkedHashMap<>());
        if (result.message != null) {
            return message.withUnsignedContent(Text.literal(result.message));
        }
        result.ifDeferredMessagePresent(s ->
                player.server.getPlayerManager().broadcast(Text.literal(s), false)
        );
        return message;
    }
}
