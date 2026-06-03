package net.meatwo310.tsukichat.mixin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.meatwo310.tsukichat.commands.ServerDictionaryCommand;
import net.meatwo310.tsukichat.commands.UserDictionaryCommand;
import net.meatwo310.tsukichat.event.ServerChat;
import net.meatwo310.tsukichat.util.ChatCustomizer;
import net.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Mixin(TeamMsgCommand.class)
public class TeamMsgCommandMixin {
    @Inject(at = @At(
            value = "INVOKE",
            target = "net/minecraft/commands/arguments/MessageArgument.resolveChatMessage(" +
                    "Lcom/mojang/brigadier/context/CommandContext;" +
                    "Ljava/lang/String;" +
                    "Ljava/util/function/Consumer;" +
                    ")V"),
            method = "lambda$register$0(" +
                    "Lcom/mojang/brigadier/context/CommandContext;" +
                    ")I",
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void resolveChatMessage(
            CommandContext<CommandSourceStack> ctx,
            CallbackInfoReturnable<Integer> cir,
            CommandSourceStack commandsourcestack,
            Entity entity,
            PlayerTeam playerteam,
            List<ServerPlayer> list
    ) throws CommandSyntaxException {
        Component message = MessageArgument.getMessage(ctx, "message");
        String original = message.getString();
        Set<String> playerTags = entity.entityTags();
        CompoundTag dict = PlayerNbtUtil.loadCompoundTag((ServerPlayer) entity, UserDictionaryCommand.KEY_NAME);
        LinkedHashMap<String, String> userDictionary = new LinkedHashMap<>();
        dict.keySet().forEach(k -> userDictionary.put(k, dict.getStringOr(k, "")));
        var serverDictionary = ServerDictionaryCommand.getServerDictionary();

        var result = ChatCustomizer.recognizeChat(original, playerTags, userDictionary, serverDictionary);
        var converted = result.getMessageSynced();
        if (converted == null) return;

        ServerChat.sendTeamMessage(
                commandsourcestack.getServer(),
                (ServerPlayer) entity,
                playerteam,
                list,
                converted
        );

        cir.setReturnValue(list.size());
    }
}
