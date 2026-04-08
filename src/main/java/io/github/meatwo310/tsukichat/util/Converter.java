package io.github.meatwo310.tsukichat.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.meatwo310.tsukichat.TsukiChat;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Converter {
    private static final Logger LOGGER = TsukiChat.LOGGER;
    private static final RomajiToHiragana ROMAJI_TO_HIRAGANA = RomajiToHiragana.fromResource(
            "/assets/japaneseromajiconverter/romaji_to_hiragana.txt",
            LOGGER::warn
    );

    /**
     * ローマ字をひらがなに変換する
     * @param romaji ローマ字
     * @return ひらがな
     */
    public static String romajiToHiragana(String romaji) {
        return ROMAJI_TO_HIRAGANA.convert(romaji);
    }

    /**
     * ひらがなを必要に応じて分割しながら日本語に変換する
     * @param hiragana ひらがな
     * @return 日本語
     */
    public static String hiraganaToJapanese(String hiragana) {
        // 空白で分割して並列で変換し、変換結果に空の要素があれば代わりにひらがなにフォールバックしてエラーを付加
        String[] parts = hiragana.split(" ");
        String[] result = Arrays.stream(parts)
                .parallel()
                .map(Converter::hiraganaPartsToJapanese)
                .toArray(String[]::new);
        boolean fallback = Arrays.stream(result).anyMatch(String::isEmpty);
        if (!fallback) return String.join(" ", result);
        for (int i = 0; i < result.length; i++) {
            if (!result[i].isEmpty()) continue;
            result[i] = parts[i];
        }
        return String.join(" ", result) + " §8(長すぎます、スペースで区切ってください)§r";
    }

    /**
     * ひらがなを日本語に変換する
     * @param hiragana ひらがな
     * @return 日本語
     */
    private static String hiraganaPartsToJapanese(String hiragana) {
        try {
            // GoogleのAPIを使って変換
            URL url = new URL("https://www.google.com/transliterate?langpair=ja-Hira|ja&text=" +
                    URLEncoder.encode(hiragana, StandardCharsets.UTF_8));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder content;
            try (Stream<String> lines = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)).lines()) {
                content = lines.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
            }
            in.close();
            conn.disconnect();

            // GSONを使ってJSONとして解析
            JsonElement jsonElement = JsonParser.parseString(content.toString());
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            // 配列をループし、最初の変換結果を選択
            StringBuilder result = new StringBuilder();
            for (JsonElement element : jsonArray) {
                JsonArray innerArray = element.getAsJsonArray();
                String firstConversionResult = innerArray.get(1).getAsJsonArray().get(0).getAsString();
                result.append(firstConversionResult);
            }

            // 全角ASCII文字を半角に変換して返す
            return Pattern
                    .compile("([！-～])")
                    .matcher(result)
                    .replaceAll(m -> String.valueOf((char) (m.group().charAt(0) - 0xFEE0)));
        } catch (Exception e) {
            LOGGER.error("Error during conversion to Japanese: ", e);
            return hiragana + " §8(エラー)§r";
        }
    }

    /**
     * ローマ字を日本語に変換する
     * @param romaji ローマ字
     * @return 日本語
     */
    public static String romajiToJapanese(String romaji) {
        return hiraganaToJapanese(romajiToHiragana(romaji));
    }

    /**
     * &を§に変換する
     * @param text テキスト
     * @return 変換後のテキスト
     */
    public static String ampersandToFormattingCode(String text) {
        return text.replaceAll("&([0-9a-fk-or])", "§$1");
    }

    /**
     * マークダウンをフォーマットコードに変換する
     * @param text テキスト
     * @return 変換後のテキスト
     */
    public static String markdownToFormattingCode(String text) {
        // TODO:入り組んだMarkdownを上手に変換する
        return text
                .replaceAll("[*＊][*＊](.*?)[*＊][*＊]", "§l$1§r")
                .replaceAll("[*＊](.*?)[*＊]", "§o$1§r")
                .replaceAll("[_＿][_＿](.*?)[_＿][_＿]", "§n$1§r")
                .replaceAll("[~〜][~〜](.*?)[~〜][~〜]", "§m$1§r");
    }
}
