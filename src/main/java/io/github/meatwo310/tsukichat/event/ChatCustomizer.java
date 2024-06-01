package io.github.meatwo310.tsukichat.event;

import io.github.meatwo310.tsukichat.config.CommonConfigs;
import io.github.meatwo310.tsukichat.util.Converter;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ChatCustomizer {
    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        // クライアントサイドでは実行しない
        ServerPlayer player = event.getPlayer();
        if (player.level.isClientSide()) return;

        String original = event.getMessage();

        // 空であれば変換しない
        if (original.isEmpty()) return;
        // 接頭辞がコンフィグによって完全無視リストに含まれていれば変換しない
        if (CommonConfigs.ignoreCompletely.get().stream().anyMatch(original::startsWith)) return;

        String amp = CommonConfigs.ampersand.get() ? Converter.ampersandToFormattingCode(original) : original;
        String converted = CommonConfigs.markdown.get() ? Converter.markdownToFormattingCode(amp) : amp;
        String result;
        if (CommonConfigs.ignore.get().stream().anyMatch(original::startsWith)) {
            // 接頭辞がコンフィグによって無視リストに含まれている場合、無視リストのフォーマットで表示する
            result = CommonConfigs.formatOnIgnore.get()
                    .replace("$0", original.substring(0, 1))
                    .replace("$1", converted.substring(1))
                    .replace("$2", original);
        } else if (CommonConfigs.ignoreNonAscii.get() && !original.matches("^[!-~\\s§]+$")) {
            // コンフィグで設定されている場合、ASCII文字以外を含むメッセージを変換しない
            result = CommonConfigs.format.get()
                    .replace("$1", converted)
                    .replace("$2", original);
        } else {
            // 通常通りかな漢字変換
            String japanese = CommonConfigs.transliterate.get() ? Converter.romajiToJapanese(converted) : converted;

            // 変換前後で文字列が変わらない場合は変換しない
            if (original.equals(japanese)) return;

            result = CommonConfigs.format.get()
                    .replace("$1", japanese)
                    .replace("$2", original);
        }

        event.setComponent(new TranslatableComponent(
                "chat.type.text",
                new TextComponent("")
                        .setStyle(event.getComponent().plainCopy().getStyle())
                        .append(player.getDisplayName()),
                new TextComponent(result))
        );
    }
}
