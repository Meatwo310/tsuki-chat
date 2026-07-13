package net.meatwo310.tsukichat.util;

import net.meatwo310.tsukichat.config.CommonConfigs;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.function.Function;

public class ChatCustomizer {
    static final String DICT_PREFIX = "[DICT_";
    static final String DICT_SUFFIX = "]";

    public static CustomizedChat recognizeChat(String original, Set<String> playerTags, LinkedHashMap<String, String> userDictionary, LinkedHashMap<String, String> serverDictionary) {
        CustomizedChat empty = new CustomizedChat();

        // メッセージが空の場合は何もしない
        if (original.isEmpty()) return empty;
        // Tsukichat無効化タグが付与されている場合は何もしない
        if (playerTags.contains(CommonConfigs.ignoreCompletelyTag.get())) return empty;
        // 無視リストにあるメッセージの場合は何もしない
        if (CommonConfigs.ignoreCompletely.get().stream().anyMatch(original::startsWith)) return empty;

        // 一部のコンフィグを取得
        String formatOriginal = CommonConfigs.formatOriginal.get();
        String formatConverted = CommonConfigs.formatConverted.get();
        String formatOriginalIgnored = CommonConfigs.formatOriginalIgnored.get();
        String formatConvertedIgnored = CommonConfigs.formatConvertedIgnored.get();

        // 無視タグを持っているかどうか
        boolean onlyMarkdown = playerTags.contains(CommonConfigs.ignoreTag.get());

        // 辞書をマージ
        LinkedHashMap<String, String> mergedDictionary = new LinkedHashMap<>(serverDictionary);
        mergedDictionary.putAll(userDictionary);

        // メッセージを変換
        String amp = CommonConfigs.ampersand.get() ? Converter.ampersandToFormattingCode(original) : original;
        String converted = CommonConfigs.markdown.get() ? Converter.markdownToFormattingCode(amp) : amp;

        String result;

        if (CommonConfigs.ignoreMessages.get().contains(original)) {
            // メッセージが無視リストにある場合
            return empty;
        } else if (!onlyMarkdown && CommonConfigs.ignore.get().stream().anyMatch(original::startsWith)) {
            // 接頭辞がコンフィグの無視リストにある場合
            if (original.equals(converted)) result = formatOriginalIgnored
                    .replace("$0", original.substring(0, 1))
                    .replace("$1", original.substring(1));
            else result = formatOriginal.replace("$0", original) + "\n" +
                    formatConvertedIgnored
                            .replace("$0", original.substring(0, 1))
                            .replace("$1", converted.substring(1));
        } else if (onlyMarkdown ||
                (CommonConfigs.ignoreNonAscii.get() && !original.matches("^[!-~\\s§]+$")) ||
                !original.matches(".*(?<!&)[a-z].*") ||
                original.length() <= CommonConfigs.ignoreLength.get()) {
            // 以下のうちいずれかに該当する場合はマークダウン変換のみ行う:
            // - プレイヤーがMarkdown変換のみ行うよう設定した場合
            // - ASCII文字以外を含み、かつコンフィグでそれを含むメッセージを無視するよう設定されている場合
            // - 小文字のアルファベットを含まない場合
            // - 文字数がコンフィグで設定された長さに満たない場合
            if (original.equals(converted)) return empty;
            result = formatOriginal.replace("$0", original) + "\n" +
                    formatConverted.replace("$0", converted);
        } else if (!CommonConfigs.transliterate.get()) {
            // ローマ字変換のみの場合
            result = formatOriginal.replace("$0", original) + "\n" +
                    formatConverted.replace("$0", applyDictionary(converted, mergedDictionary, Converter::romajiToHiragana));
        } else {
            // 日本語変換する場合
            if (CommonConfigs.multiThreading.get()) {
                return new CustomizedChat(
                        formatOriginal.replace("$0", original),
                        () -> {
                            String hiragana = applyDictionary(converted, mergedDictionary, Converter::romajiToHiragana);
                            String japanese = applyDictionary(hiragana, mergedDictionary, Converter::hiraganaToJapanese);
                            return formatConverted.replace("$0", japanese);
                        }
                );
            } else {
                String hiragana = applyDictionary(converted, mergedDictionary, Converter::romajiToHiragana);
                String japanese = applyDictionary(hiragana, mergedDictionary, Converter::hiraganaToJapanese);
                result = formatOriginal.replace("$0", original) + "\n" +
                        formatConverted.replace("$0", japanese);
            }
        }

        return new CustomizedChat(result);
    }

    public static String applyDictionary(String original, LinkedHashMap<String, String> dictionary, Function<String, String> function) {
        String intermediate = original;
        int i = 0;
        for (String key : dictionary.keySet()) {
            intermediate = intermediate.replace(key, DICT_PREFIX + i++ + DICT_SUFFIX);
        }

        String output = function.apply(intermediate);
        i = 0;
        for (String key : dictionary.keySet()) {
            output = output.replace(DICT_PREFIX + i++ + DICT_SUFFIX, dictionary.get(key));
        }
        return output;
    }
}
