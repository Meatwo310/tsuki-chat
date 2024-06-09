package io.github.meatwo310.tsukichat.config;

import io.github.meatwo310.tsukichat.TsukiChat;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = TsukiChat.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonConfigs {
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec.BooleanValue transliterate = builder
            .comment("ローマ字を漢字混じりの日本語に変換するかどうか。\n" +
                    "無効にすると、ローマ字変換のみ行います。")
            .define("transliterate", true);
    public static ForgeConfigSpec.BooleanValue ignoreNonAscii = builder
            .comment("ASCII文字以外を含むメッセージのローマ字変換と日本語変換を無視するかどうか。\n" +
                    "このオプションは、マークダウン変換を行うかどうかに影響しません。")
            .define("ignore_non_ascii", true);
    public static ForgeConfigSpec.BooleanValue ampersand = builder
            .comment("&を§に変換するかどうか。")
            .define("ampersand", true);
    public static ForgeConfigSpec.BooleanValue markdown = builder
            .comment("単純なマークダウンを装飾コードに変換するかどうか。\n" +
                    "複雑なマークダウンはうまく変換されない場合があります。")
            .define("markdown", true);
    public static ForgeConfigSpec.BooleanValue allowPersonalSettings = builder
            .comment("プレイヤーが /tsukichat で個人設定を変更できるようにするかどうか。\n" +
                    "無効にした場合でも、ignoreTagとignoreCompletelyTagは使用されます。")
            .define("allow_personal_settings", true);
    public static ForgeConfigSpec.BooleanValue multiThreading = builder
            .comment("""
                    ローマ字から日本語への変換をマルチスレッドで行うかどうか。
                    無効化した場合、他MODとの互換性が向上する代償に、
                    サーバーのtick処理に顕著な遅延が生じる可能性があります。""")
            .define("multi_threading", true);

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ignore = builder
            .comment("TsukiChatは、以下の接頭辞から始まるメッセージのローマ字変換や日本語変換を行いません。\n" +
                    "ただし、マークダウンの変換は行われます。")
            .defineList("ignore", List.of("!", "#", ";"), o -> true);
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ignoreCompletely = builder
            .comment("TsukiChatは、以下の接頭辞から始まるメッセージについて、一切の変換を行いません。")
            .defineList("ignore_completely", List.of(":"), o -> true);
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ignoreMessages = builder
            .comment("メッセージが以下のリストのいずれかと一致する場合、一切の変換を行いません。")
            .defineList("ignore_messages", List.of(
                    "afk",
                    "bad",
                    "brb",
                    "btw",
                    "cool",
                    "ez",
                    "false",
                    "fine",
                    "gg",
                    "gj",
                    "gl",
                    "good",
                    "great",
                    "hello",
                    "hf",
                    "hi",
                    "idk",
                    "jk",
                    "lmao",
                    "lol",
                    "maybe",
                    "nc",
                    "nice",
                    "no",
                    "nope",
                    "np",
                    "nvm",
                    "oh",
                    "ok",
                    "okay",
                    "omg",
                    "please",
                    "pls",
                    "plz",
                    "rofl",
                    "sorry",
                    "sry",
                    "sure",
                    "thanks",
                    "thx",
                    "true",
                    "ty",
                    "tysm",
                    "w",
                    "wow",
                    "wtf",
                    "ww",
                    "www",
                    "wwww",
                    "wwwww",
                    "wwwwww",
                    "wwwwwww",
                    "wwwwwwww",
                    "wwwwwwwww",
                    "wwwwwwwwww",
                    "yay",
                    "yeah",
                    "yes",
                    "yw"
            ), o -> true);

    public static ForgeConfigSpec.IntValue ignoreLength = builder
            .comment("変換前のメッセージの長さがこの値以下の場合、ローマ字変換や日本語変換を行いません。\n" +
                    "ただし、マークダウンの変換は行われます。")
            .defineInRange("ignore_length", 3, 0, Integer.MAX_VALUE);

    public static ForgeConfigSpec.ConfigValue<String> formatOriginal = builder
            .comment("変換前のメッセージをどう表示するかを指定します。\n" +
                    "$0は変換前のメッセージに置き換えられます。")
            .define("format_original", "§7$0§r");
    public static ForgeConfigSpec.ConfigValue<String> formatConverted = builder
            .comment("変換後のメッセージをどう表示するかを指定します。\n" +
                    "$0は変換後のメッセージに置き換えられます。")
            .define("format_converted", "→ $0§r");
    public static ForgeConfigSpec.ConfigValue<String> formatOriginalIgnored = builder
            .comment("コンフィグignoreで設定された接頭辞で始まるメッセージがMarkdown変換されなかった際にどう表示するかを指定します。\n" +
                    "$0は接頭辞、$1はメッセージのうち接頭辞以外の部分に置き換えられます。")
            .define("format_original_ignored", "§7$0§r$1§r");
    public static ForgeConfigSpec.ConfigValue<String> formatConvertedIgnored = builder
            .comment("コンフィグignoreで設定された接頭辞から始まるメッセージがMarkdown変換された際にどう表示するかを指定します。\n" +
                    "$0は接頭辞、$1は変換後のメッセージのうち接頭辞以外の部分に置き換えられます。")
            .define("format_converted_ignored", "→ §7$0§r$1§r");

    public static ForgeConfigSpec.ConfigValue<String> ignoreTag = builder
            .comment("TsukiChatは、以下のタグを持つプレイヤーのメッセージのローマ字変換や日本語変換を行いません。\n" +
                    "ただし、マークダウンの変換は行われます。")
            .define("ignore_tag", "tsukichat_no_romaji");
    public static ForgeConfigSpec.ConfigValue<String> ignoreCompletelyTag = builder
            .comment("TsukiChatは、以下のタグを持つプレイヤーのメッセージについて、一切の変換を行いません。")
            .define("ignore_completely_tag", "tsukichat_ignore");

    public static final ForgeConfigSpec COMMON_SPEC = builder.build();
}
