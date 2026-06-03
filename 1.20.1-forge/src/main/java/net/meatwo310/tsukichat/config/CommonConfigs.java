package net.meatwo310.tsukichat.config;

import net.meatwo310.tsukichat.ModMain;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
                    [非推奨] ローマ字から日本語への変換をTsukiChat側のマルチスレッドで行うかどうか。
                    比較的新しいMinecraftではForgeがイベントをマルチスレッドで処理するため、
                    この設定を有効化する必要はありません。""")
            .define("multi_threading", false);
    public static ForgeConfigSpec.BooleanValue sdlinkCompat = builder
            .comment("Simple Discord Link上でtsukichatを作用させるかどうか。\n" +
                    "無効にすると、Simple Discord Linkは従来どおり変換前のメッセージを使用します。")
            .define("sdlink_compat", true);
    public static ForgeConfigSpec.BooleanValue allowAddingServerDictionary = builder
            .comment("通常のプレイヤーがサーバー辞書に単語を追加できるようにするかどうか。\n" +
                    "無効にした場合でも、OP権限を持つプレイヤーは辞書を管理することができます。")
            .define("allow_add_global_dictionary", true);
    public static ForgeConfigSpec.BooleanValue allowRemovingServerDictionary = builder
            .comment("通常のプレイヤーがサーバー辞書から単語を削除できるようにするかどうか。\n" +
                    "無効にした場合でも、OP権限を持つプレイヤーは辞書を管理することができます。")
            .define("allow_remove_global_dictionary", true);
    public static ForgeConfigSpec.BooleanValue mohistCompat = builder
            .comment("""
                    Mohist上でSpigotのイベントシステムを使用するかどうか。
                    Mohist上でForgeのServerChatEventが発火しない問題の回避策となります。
                    Mohist環境下でない場合、このオプションは単に無視されます。""")
            .define("mohist_compat", true);

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ignore = builder
            .comment("TsukiChatは、以下の接頭辞から始まるメッセージのローマ字変換や日本語変換を行いません。\n" +
                    "ただし、マークダウンの変換は行われます。")
            .defineList("ignore", List.of("#", ";"), o -> true);
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ignoreCompletely = builder
            .comment("TsukiChatは、以下の接頭辞から始まるメッセージについて、一切の変換を行いません。")
            .defineList("ignore_completely", List.of(":", "./"), o -> true);
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

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> serverDictionary = builder
            .comment("サーバー辞書。ここに登録された単語は、すべてのプレイヤーのメッセージに対して適用されます。\n" +
                    "キーと値は、タブ文字\\tで区切ってください。")
            .defineList("server_dictionary", List.of(
                    "TsukiChat\tTsukiChat",
                    "Minecraft\tMinecraft",
                    "Forge\tForge",
                    "Fabric\tFabric",
                    "Mod\tMod",
                    "NeoForge\tNeoForge",
                    "Google\tGoogle",
                    "GitHub\tGitHub",
                    "Java\tJava",
                    "っw\tww"
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
            .define("format_converted", "→ $0");
    public static ForgeConfigSpec.ConfigValue<String> formatOriginalIgnored = builder
            .comment("コンフィグignoreで設定された接頭辞で始まるメッセージがMarkdown変換されなかった際にどう表示するかを指定します。\n" +
                    "$0は接頭辞、$1はメッセージのうち接頭辞以外の部分に置き換えられます。")
            .define("format_original_ignored", "§7$0§r$1§r");
    public static ForgeConfigSpec.ConfigValue<String> formatConvertedIgnored = builder
            .comment("コンフィグignoreで設定された接頭辞から始まるメッセージがMarkdown変換された際にどう表示するかを指定します。\n" +
                    "$0は接頭辞、$1は変換後のメッセージのうち接頭辞以外の部分に置き換えられます。")
            .define("format_converted_ignored", "→ §7$0§r$1");

    public static ForgeConfigSpec.ConfigValue<String> ignoreTag = builder
            .comment("TsukiChatは、以下のタグを持つプレイヤーのメッセージのローマ字変換や日本語変換を行いません。\n" +
                    "ただし、マークダウンの変換は行われます。")
            .define("ignore_tag", "tsukichat_no_romaji");
    public static ForgeConfigSpec.ConfigValue<String> ignoreCompletelyTag = builder
            .comment("TsukiChatは、以下のタグを持つプレイヤーのメッセージについて、一切の変換を行いません。")
            .define("ignore_completely_tag", "tsukichat_ignore");

    public static final ForgeConfigSpec.BooleanValue formatTeamMsg = builder
            .comment("チームメッセージを変換するかどうか。")
            .define("format_team_msg", true);

    public static final ForgeConfigSpec.BooleanValue defaultTeamMsg = builder
            .comment("""
                    送信されたメッセージをデフォルトでチームメッセージとして送信するかどうか。
                    チームに所属していない場合は機能しません。
                    コンフィグforce_globalにマッチするメッセージはチームメッセージとして送信されません。""")
            .define("default_team_msg", false);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> forceGlobal = builder
            .comment("TsukiChatは、以下の接頭辞から始まるメッセージを強制的にグローバルチャットとして扱います。\n" +
                    "コンフィグdefault_team_msgが無効の場合は機能しません。")
            .defineList("force_global", List.of("!"), o -> true);

    public static final ForgeConfigSpec.IntValue forwardTeamMsgLevel = builder
            .comment("すべてのチームメッセージを指定された権限レベルを持つプレイヤーへ転送します。\n" +
                    "-1が指定されている場合、チームメッセージは転送されません。")
            .defineInRange("forward_team_msg_level", -1, -1, Integer.MAX_VALUE);

    public static final ForgeConfigSpec COMMON_SPEC = builder.build();
}
