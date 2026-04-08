package io.github.meatwo310.tsukichat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ローマ字からひらがなへの変換を行うクラス。
 * Minecraft / Forge に依存しない純粋なJavaクラス。
 */
public class RomajiToHiragana {

    /**
     * 変換テーブルの1エントリ。
     * @param hiragana 変換先のひらがな（またはそのまま出力する文字列）
     * @param backtrack 巻き戻り文字数（通常0、促音などで負の値）
     */
    record Entry(String hiragana, int backtrack) {}

    private final Map<String, Entry> conversionTable;
    private final int maxKeyLength;

    RomajiToHiragana(Map<String, Entry> conversionTable) {
        this.conversionTable = Map.copyOf(conversionTable);
        this.maxKeyLength = conversionTable.keySet().stream()
                .mapToInt(String::length)
                .max()
                .orElse(0);
    }

    /**
     * リソースファイルから変換テーブルを読み込んでインスタンスを生成する。
     *
     * <p>ファイル形式（1行ごと）:</p>
     * <ul>
     *   <li>{@code ローマ字:ひらがな} — 巻き戻りなし</li>
     *   <li>{@code ローマ字:ひらがな:巻き戻り数} — 促音など巻き戻りあり</li>
     * </ul>
     *
     * @param resourcePath クラスパス上のリソースパス
     * @param logger 警告ログの出力先（nullの場合はログを出力しない）
     * @return 生成されたインスタンス
     * @throws IllegalArgumentException リソースが見つからない場合
     * @throws RuntimeException 読み込み中にIOエラーが発生した場合
     */
    public static RomajiToHiragana fromResource(String resourcePath, Logger logger) {
        InputStream stream = RomajiToHiragana.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }

        LinkedHashMap<String, Entry> table = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                parseLine(line, resourcePath, table, logger);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource: " + resourcePath, e);
        }

        return new RomajiToHiragana(table);
    }

    /**
     * ローマ字をひらがなに変換する。
     * 変換テーブルに一致しない文字はそのまま出力される。
     *
     * @param romaji 変換元のローマ字文字列
     * @return 変換後のひらがな文字列
     */
    public String convert(String romaji) {
        StringBuilder result = new StringBuilder();
        int pos = 0;

        while (pos < romaji.length()) {
            Entry matched = null;
            int matchedLength = 0;

            // 最長一致: 長い候補から順に試す
            int maxLen = Math.min(maxKeyLength, romaji.length() - pos);
            for (int len = maxLen; len >= 1; len--) {
                String candidate = romaji.substring(pos, pos + len);
                Entry entry = conversionTable.get(candidate);
                if (entry != null) {
                    matched = entry;
                    matchedLength = len;
                    break;
                }
            }

            if (matched != null) {
                result.append(matched.hiragana());
                pos += matchedLength + matched.backtrack();
            } else {
                // 一致なし: 1文字そのまま出力
                result.append(romaji.charAt(pos));
                pos++;
            }
        }

        return result.toString();
    }

    /**
     * 変換テーブルの1行をパースしてテーブルに追加する。
     */
    private static void parseLine(String line, String resourceName,
                                  Map<String, Entry> table, Logger logger) {
        String[] parts = line.split(":");
        if (parts.length == 2) {
            table.put(parts[0], new Entry(parts[1], 0));
        } else if (parts.length == 3) {
            try {
                int backtrack = Integer.parseInt(parts[2]);
                table.put(parts[0], new Entry(parts[1], backtrack));
            } catch (NumberFormatException e) {
                if (logger != null) {
                    logger.warn("Could not parse backtrack value in {}; line ignored: {}", resourceName, line);
                }
            }
        } else {
            if (logger != null) {
                logger.warn("Invalid line in {}; line ignored: {}", resourceName, line);
            }
        }
    }

    /**
     * ログ出力用のインターフェース。
     * slf4j等の具体的なロガーに依存しないようにするための抽象化。
     */
    @FunctionalInterface
    public interface Logger {
        void warn(String format, Object... args);
    }
}
