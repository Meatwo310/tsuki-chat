package io.github.meatwo310.tsukichat.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class CommonConfigs {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static ForgeConfigSpec.BooleanValue transliterate;
    public static ForgeConfigSpec.BooleanValue ignoreNonAscii;
    public static ForgeConfigSpec.BooleanValue ampersand;
    public static ForgeConfigSpec.BooleanValue markdown;
    public static ForgeConfigSpec.BooleanValue allowPersonalSettings;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ignore;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ignoreCompletely;
    public static ForgeConfigSpec.IntValue ignoreLength;
    public static ForgeConfigSpec.ConfigValue<String> formatOriginal;
    public static ForgeConfigSpec.ConfigValue<String> formatConverted;
    public static ForgeConfigSpec.ConfigValue<String> formatOriginalIgnored;
    public static ForgeConfigSpec.ConfigValue<String> formatConvertedIgnored;
    public static ForgeConfigSpec.ConfigValue<String> ignoreTag;
    public static ForgeConfigSpec.ConfigValue<String> ignoreCompletelyTag;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        transliterate = builder
                .comment("ローマ字を漢字混じりの日本語に変換するかどうか。\n" +
                        "無効にすると、ローマ字変換のみ行います。")
                .define("transliterate", true);
        ignoreNonAscii = builder
                .comment("ASCII文字以外を含むメッセージのローマ字変換と日本語変換を無視するかどうか。\n" +
                        "このオプションは、マークダウン変換を行うかどうかに影響しません。")
                .define("ignore_non_ascii", true);
        ampersand = builder
                .comment("&を§に変換するかどうか。")
                .define("ampersand", true);
        markdown = builder
                .comment("単純なマークダウンを装飾コードに変換するかどうか。\n" +
                        "複雑なマークダウンはうまく変換されない場合があります。")
                .define("markdown", true);
        allowPersonalSettings = builder
                .comment("プレイヤーが /tsukichat で個人設定を変更できるようにするかどうか。\n" +
                        "無効にした場合でも、ignoreTagとignoreCompletelyTagは使用されます。")
                .define("allow_personal_settings", true);

        ignore = builder
                .comment("TsukiChatは、以下の接頭辞から始まるメッセージのローマ字変換や日本語変換を行いません。\n" +
                        "ただし、マークダウンの変換は行われます。")
                .defineList("ignore", List.of("!", "#", ";"), o -> true);
        ignoreCompletely = builder
                .comment("TsukiChatは、以下の接頭辞から始まるメッセージについて、一切の変換を行いません。")
                .defineList("ignore_completely", List.of(":"), o -> true);

        ignoreLength = builder
                .comment("変換前のメッセージの長さがこの値以下の場合、ローマ字変換や日本語変換を行いません。\n" +
                        "ただし、マークダウンの変換は行われます。")
                .defineInRange("ignore_length", 3, 0, Integer.MAX_VALUE);

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

        ignoreTag = builder
                .comment("TsukiChatは、以下のタグを持つプレイヤーのメッセージのローマ字変換や日本語変換を行いません。\n" +
                        "ただし、マークダウンの変換は行われます。")
                .define("ignore_tag", "tsukichat_no_romaji");
        ignoreCompletelyTag = builder
                .comment("TsukiChatは、以下のタグを持つプレイヤーのメッセージについて、一切の変換を行いません。")
                .define("ignore_completely_tag", "tsukichat_ignore");



        COMMON_SPEC = builder.build();
    }
}
