package io.github.meatwo310.tsukichat.event;

import io.github.meatwo310.tsukichat.config.CommonConfigs;
import io.github.meatwo310.tsukichat.util.Converter;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mod.EventBusSubscriber
public class ChatCustomizer {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        // クライアントサイドでは実行しない
        ServerPlayer player = event.getPlayer();
        if (player.level.isClientSide()) return;

        String original = event.getMessage();

        if (original.isEmpty()) return;

        // 接頭辞がコンフィグによって完全無視リストに含まれていれば変換しない
        if (CommonConfigs.ignoreCompletely.get().stream().anyMatch(original::startsWith)) return;

        String amp = CommonConfigs.ampersand.get() ? Converter.ampersandToFormattingCode(original) : original;
        String converted = CommonConfigs.markdown.get() ? Converter.markdownToFormattingCode(amp) : amp;
        String result;
        if (CommonConfigs.ignore.get().stream().anyMatch(original::startsWith)) {
            // 接頭辞がコンフィグの無視リストにある場合
            if (original.equals(converted)) result = CommonConfigs.formatOriginalIgnored.get()
                    .replace("$0", original.substring(0, 1))
                    .replace("$1", original.substring(1));
            else result = CommonConfigs.formatOriginal.get().replace("$0", original) + "\n" +
                    CommonConfigs.formatConvertedIgnored.get()
                            .replace("$0", original.substring(0, 1))
                            .replace("$1", converted.substring(1));
        } else if ((CommonConfigs.ignoreNonAscii.get() && !original.matches("^[!-~\\s§]+$")) || !original.matches(".*(?<!&)[a-z].*")) {
            // コンフィグで設定されているかつASCII文字以外を含む場合、または小文字のアルファベットを含まない場合は、マークダウン変換のみ行う
            if (original.equals(converted)) return;
            result = CommonConfigs.formatOriginal.get().replace("$0", original) + "\n" +
                    CommonConfigs.formatConverted.get().replace("$0", converted);
        } else if (!CommonConfigs.transliterate.get()) {
            // ローマ字変換のみの場合
            result = CommonConfigs.formatOriginal.get().replace("$0", original) + "\n" +
                    CommonConfigs.formatConverted.get().replace("$0", Converter.romajiToHiragana(converted));
        } else {
            // 日本語変換する場合
            result = CommonConfigs.formatOriginal.get().replace("$0", original);

            // 日本語への変換は非同期で行う
            executorService.submit(() -> {
                String japanese = CommonConfigs.transliterate.get() ? Converter.romajiToJapanese(converted) : converted;

                var component = new TextComponent(CommonConfigs.formatConverted.get()
                        .replace("$0", japanese));
                player.server.getPlayerList().broadcastMessage(component, ChatType.CHAT, player.getUUID());
            });
        }

        event.setComponent(new TranslatableComponent(
                "chat.type.text", player.getDisplayName(), result)
        );
    }
}
