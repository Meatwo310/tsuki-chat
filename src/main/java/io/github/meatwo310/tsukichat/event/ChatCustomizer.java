package io.github.meatwo310.tsukichat.event;

import io.github.meatwo310.tsukichat.commands.UserDictionaryCommand;
import io.github.meatwo310.tsukichat.config.CommonConfigs;
import io.github.meatwo310.tsukichat.util.Converter;
import io.github.meatwo310.tsukichat.util.PlayerNbtUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Mod.EventBusSubscriber
public class ChatCustomizer {
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        // クライアントサイドでは実行しない
        ServerPlayer player = event.getPlayer();
        if (player.level.isClientSide()) return;

        String original = event.getRawText();

        if (original.isEmpty()) return;
        if (player.getTags().contains(CommonConfigs.ignoreCompletelyTag.get())) return;
        if (CommonConfigs.ignoreCompletely.get().stream().anyMatch(original::startsWith)) return;

        boolean transliterate = CommonConfigs.transliterate.get();
        boolean ignoreNonAscii = CommonConfigs.ignoreNonAscii.get();
        boolean ampersand = CommonConfigs.ampersand.get();
        boolean markdown = CommonConfigs.markdown.get();
        boolean multiThreading = CommonConfigs.multiThreading.get();
        List<? extends String> ignore = CommonConfigs.ignore.get();
        List<? extends String> ignoreMessages = CommonConfigs.ignoreMessages.get();
        int ignoreLength = CommonConfigs.ignoreLength.get();
        String formatOriginal = CommonConfigs.formatOriginal.get();
        String formatConverted = CommonConfigs.formatConverted.get();
        String formatOriginalIgnored = CommonConfigs.formatOriginalIgnored.get();
        String formatConvertedIgnored = CommonConfigs.formatConvertedIgnored.get();
        boolean ignoreTag = player.getTags().contains(CommonConfigs.ignoreTag.get());

        String amp = ampersand ? Converter.ampersandToFormattingCode(original) : original;
        String converted = markdown ? Converter.markdownToFormattingCode(amp) : amp;
        String result;

        if (ignoreMessages.contains(original)) {
            // メッセージが無視リストにある場合
            return;
        } else if (!ignoreTag && ignore.stream().anyMatch(original::startsWith)) {
            // 接頭辞がコンフィグの無視リストにある場合
            if (original.equals(converted)) result = formatOriginalIgnored
                    .replace("$0", original.substring(0, 1))
                    .replace("$1", original.substring(1));
            else result = formatOriginal.replace("$0", original) + "\n" +
                    formatConvertedIgnored
                            .replace("$0", original.substring(0, 1))
                            .replace("$1", converted.substring(1));
        } else if (ignoreTag ||
                (ignoreNonAscii && !original.matches("^[!-~\\s§]+$")) ||
                !original.matches(".*(?<!&)[a-z].*") ||
                original.length() <= ignoreLength) {
            // 以下のいずれかの場合はマークダウン変換のみ行う:
            // - プレイヤーが無視タグを持っている場合
            // - ASCII文字以外を含み、かつコンフィグでそれを無視するよう設定されている場合
            // - 小文字のアルファベットを含まない場合
            // - 文字数が無視する長さ以下の場合
            if (original.equals(converted)) return;
            result = formatOriginal.replace("$0", original) + "\n" +
                    formatConverted.replace("$0", converted);
        } else if (!transliterate) {
            // ローマ字変換のみの場合
            CompoundTag tag = PlayerNbtUtil.loadCompoundTag(player, UserDictionaryCommand.KEY_NAME);
            result = formatOriginal.replace("$0", original) + "\n" +
                    formatConverted.replace("$0", applyDictionary(converted, tag, Converter::romajiToHiragana));
        } else {
            // 日本語変換する場合
            result = formatOriginal.replace("$0", original);

            if (multiThreading) {
                executorService.submit(() -> {
                    CompoundTag tag = PlayerNbtUtil.loadCompoundTag(player, UserDictionaryCommand.KEY_NAME);
                    String hiragana = applyDictionary(converted, tag, Converter::romajiToHiragana);
                    String japanese = applyDictionary(hiragana, tag, Converter::hiraganaToJapanese);
                    MutableComponent component = Component.literal(formatConverted.replace("$0", japanese));
                    player.server.getPlayerList().broadcastSystemMessage(component, false);
                    //                    PlayerChatMessage chatMessage = PlayerChatMessage.unsigned(player.getUUID(), formatConverted.replace("$0", japanese));
                    //                    player.createCommandSourceStack().sendChatMessage(new OutgoingChatMessage.Player(chatMessage), false, ChatType.bind(ChatType.EMOTE_COMMAND, player));
                });
            } else {
                CompoundTag tag = PlayerNbtUtil.loadCompoundTag(player, UserDictionaryCommand.KEY_NAME);
                String hiragana = applyDictionary(converted, tag, Converter::romajiToHiragana);
                String japanese = applyDictionary(hiragana, tag, Converter::hiraganaToJapanese);
                result += "\n" + formatConverted.replace("$0", japanese);
            }
        }

        event.setMessage(Component.literal(result));
    }

    public static String applyDictionary(String original, CompoundTag dictionary, Function<String, String> function) {
        List<Pair<String, String>> dictionaryPairList = new ArrayList<>();
        dictionary.getAllKeys().forEach(pair -> dictionaryPairList.add(Pair.of(pair, dictionary.getString(pair))));

        String intermediate = original;
        for (int i = 0; i < dictionaryPairList.size(); i++) {
            intermediate = intermediate.replace(
                    dictionaryPairList.get(i).getLeft(),
                    "[INTERMEDIATE_" + i + "]"
            );
        }

        String output = function.apply(intermediate);

        for (int i = 0; i < dictionaryPairList.size(); i++) {
            output = output.replace(
                    "[INTERMEDIATE_" + i + "]",
                    dictionaryPairList.get(i).getRight()
            );
        }

        return output;
    }
}
