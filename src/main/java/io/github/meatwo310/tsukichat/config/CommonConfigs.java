package io.github.meatwo310.tsukichat.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class CommonConfigs {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static ForgeConfigSpec.BooleanValue transliterate;
    public static ForgeConfigSpec.BooleanValue ignoreNonAscii;
    public static ForgeConfigSpec.BooleanValue ampersand;
    public static ForgeConfigSpec.BooleanValue markdown;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ignore;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ignoreCompletely;
    public static ForgeConfigSpec.ConfigValue<String> formatOriginal;
    public static ForgeConfigSpec.ConfigValue<String> formatConverted;
    public static ForgeConfigSpec.ConfigValue<String> formatOriginalIgnored;
    public static ForgeConfigSpec.ConfigValue<String> formatConvertedIgnored;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        transliterate = builder
                .comment("ローマ字を漢字混じりの日本語に変換するかどうか。\n" +
                        "無効にすると、ローマ字変換のみ行います。")
                .define("transliterate", true);
        ignoreNonAscii = builder
                .comment("ASCII文字以外を含むメッセージのローマ字変換を無視するかどうか。\n" +
                        "このオプションは、マークダウン変換を行うかどうかに影響しません。")
                .define("ignore_non_ascii", true);
        ampersand = builder
                .comment("&を§に変換するかどうか。")
                .define("ampersand", true);
        markdown = builder
                .comment("単純なマークダウンを装飾コードに変換するかどうか。\n" +
                        "複雑なマークダウンはうまく変換されない場合があります。")
                .define("markdown", true);

        ignore = builder
                .comment("TsukiChatは、以下の接頭辞から始まるメッセージのローマ字変換を行いません。\n" +
                        "ただし、マークダウンの変換は行われます。")
                .defineList("ignore", List.of("!", "#", ";"), o -> o instanceof String);
        ignoreCompletely = builder
                .comment("TsukiChatは、以下の接頭辞から始まるメッセージについて、一切の変換を行いません。")
                .defineList("ignore_completely", List.of(":"), o -> o instanceof String);

        formatOriginal = builder
                .comment("変換前のメッセージをどう表示するかを指定します。\n" +
                        "$0は変換前のメッセージに置き換えられます。")
                .define("format_original", "§7$0§r");
        formatConverted = builder
                .comment("変換後のメッセージをどう表示するかを指定します。\n" +
                        "$0は変換後のメッセージに置き換えられます。")
                .define("format_converted", "→ $0§r");
        formatOriginalIgnored = builder
                .comment("コンフィグignoreで設定された接頭辞で始まるメッセージがMarkdown変換されなかった際にどう表示するかを指定します。\n" +
                        "$0は接頭辞、$1はメッセージのうち接頭辞以外の部分に置き換えられます。")
                .define("format_original_ignored", "§7$0§r$1§r");
        formatConvertedIgnored = builder
                .comment("コンフィグignoreで設定された接頭辞から始まるメッセージがMarkdown変換された際にどう表示するかを指定します。\n" +
                        "$0は接頭辞、$1は変換後のメッセージのうち接頭辞以外の部分に置き換えられます。")
                .define("format_converted_ignored", "→ §7$0§r$1§r");

        COMMON_SPEC = builder.build();
    }
}