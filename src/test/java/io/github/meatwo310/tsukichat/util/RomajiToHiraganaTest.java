package io.github.meatwo310.tsukichat.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class RomajiToHiraganaTest {

    private static RomajiToHiragana converter;

    @BeforeAll
    static void setUp() {
        converter = RomajiToHiragana.fromResource(
                "/assets/japaneseromajiconverter/romaji_to_hiragana.txt", null);
    }

    // === 基本母音 ===

    @Nested
    class BasicVowels {
        @ParameterizedTest
        @CsvSource({
                "a, あ",
                "i, い",
                "u, う",
                "e, え",
                "o, お",
        })
        void shouldConvertVowels(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }

        @Test
        void shouldConvertAllVowelsCombined() {
            assertEquals("あいうえお", converter.convert("aiueo"));
        }
    }

    // === か行〜わ行 ===

    @Nested
    class BasicConsonantVowel {
        @ParameterizedTest
        @CsvSource({
                "ka, か", "ki, き", "ku, く", "ke, け", "ko, こ",
                "sa, さ", "si, し", "su, す", "se, せ", "so, そ",
                "ta, た", "ti, ち", "tu, つ", "te, て", "to, と",
                "na, な", "ni, に", "nu, ぬ", "ne, ね", "no, の",
                "ha, は", "hi, ひ", "hu, ふ", "he, へ", "ho, ほ",
                "ma, ま", "mi, み", "mu, む", "me, め", "mo, も",
                "ya, や",             "yu, ゆ",             "yo, よ",
                "ra, ら", "ri, り", "ru, る", "re, れ", "ro, ろ",
                "wa, わ",                                   "wo, を",
        })
        void shouldConvertBasicConsonantVowel(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }

        @ParameterizedTest
        @CsvSource({
                "ga, が", "gi, ぎ", "gu, ぐ", "ge, げ", "go, ご",
                "za, ざ", "zi, じ", "zu, ず", "ze, ぜ", "zo, ぞ",
                "da, だ", "di, ぢ", "du, づ", "de, で", "do, ど",
                "ba, ば", "bi, び", "bu, ぶ", "be, べ", "bo, ぼ",
                "pa, ぱ", "pi, ぴ", "pu, ぷ", "pe, ぺ", "po, ぽ",
        })
        void shouldConvertDakutenHandakuten(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }
    }

    // === 拗音 (きゃ, しゃ, ちゃ, etc.) ===

    @Nested
    class Youon {
        @ParameterizedTest
        @CsvSource({
                "kya, きゃ", "kyu, きゅ", "kyo, きょ",
                "gya, ぎゃ", "gyu, ぎゅ", "gyo, ぎょ",
                "sha, しゃ", "shi, し", "shu, しゅ", "sho, しょ",
                "sya, しゃ", "syu, しゅ", "syo, しょ",
                "ja, じゃ", "ji, じ", "ju, じゅ", "jo, じょ",
                "cha, ちゃ", "chi, ち", "chu, ちゅ", "cho, ちょ",
                "tya, ちゃ", "tyu, ちゅ", "tyo, ちょ",
                "nya, にゃ", "nyu, にゅ", "nyo, にょ",
                "hya, ひゃ", "hyu, ひゅ", "hyo, ひょ",
                "bya, びゃ", "byu, びゅ", "byo, びょ",
                "pya, ぴゃ", "pyu, ぴゅ", "pyo, ぴょ",
                "mya, みゃ", "myu, みゅ", "myo, みょ",
                "rya, りゃ", "ryu, りゅ", "ryo, りょ",
        })
        void shouldConvertYouon(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }
    }

    // === 促音 (っ) ===

    @Nested
    class Sokuon {
        @ParameterizedTest
        @CsvSource({
                "kka, っか", "ssa, っさ", "tta, った", "ppa, っぱ",
                "gga, っが", "zza, っざ", "dda, っだ", "bba, っば",
                "mma, っま", "rra, っら",
        })
        void shouldConvertSokuon(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }

        @Test
        void shouldConvertSokuonWithComplexFollower() {
            // kk → っ(backtrack -1) → k を再処理 → ka → か
            assertEquals("っきゃ", converter.convert("kkya"));
            assertEquals("っしゃ", converter.convert("ssha"));
            assertEquals("っちゃ", converter.convert("ccha"));
            assertEquals("っち", converter.convert("cchi"));
        }

        @Test
        void shouldHandleTripleW() {
            // www → w(backtrack -2) → pos=1, ww → っ(backtrack -1) → pos=2, w → passthrough
            assertEquals("wっw", converter.convert("www"));
        }
    }

    // === 「ん」の処理 ===

    @Nested
    class N {
        @Test
        void shouldConvertNn() {
            assertEquals("ん", converter.convert("nn"));
        }

        @Test
        void shouldConvertNApostrophe() {
            assertEquals("ん", converter.convert("n'"));
        }

        @Test
        void shouldConvertNBeforeConsonant() {
            // n の後に母音以外 → ん
            assertEquals("ほんだ", converter.convert("honda"));
            assertEquals("しんぶん", converter.convert("shinbun"));
            assertEquals("かんじ", converter.convert("kanji"));
        }

        @Test
        void shouldConvertNBeforeVowel() {
            // n の後に母音 → な行になる
            assertEquals("かな", converter.convert("kana"));
            assertEquals("あに", converter.convert("ani"));
        }

        @Test
        void shouldDistinguishNnAndNa() {
            // nn'a = ん + あ ではなく、nn → ん + a → あ
            assertEquals("んあ", converter.convert("nna"));
            // n' + a = ん + あ
            assertEquals("んあ", converter.convert("n'a"));
        }
    }

    // === 小文字（ぁぃぅぇぉ等） ===

    @Nested
    class SmallKana {
        @ParameterizedTest
        @CsvSource({
                "la, ぁ", "li, ぃ", "lu, ぅ", "le, ぇ", "lo, ぉ",
                "xa, ぁ", "xi, ぃ", "xu, ぅ", "xe, ぇ", "xo, ぉ",
                "lya, ゃ", "lyu, ゅ", "lyo, ょ",
                "xya, ゃ", "xyu, ゅ", "xyo, ょ",
                "xtsu, っ", "xtu, っ", "ltu, っ", "ltsu, っ",
                "xka, ヵ", "xke, ヶ", "lka, ヵ", "lke, ヶ",
                "xwa, ゎ", "lwa, ゎ",
        })
        void shouldConvertSmallKana(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }
    }

    // === 特殊な入力 ===

    @Nested
    class SpecialInputs {
        @Test
        void shouldConvertTsu() {
            assertEquals("つ", converter.convert("tsu"));
        }

        @Test
        void shouldConvertLongVowelMark() {
            assertEquals("ー", converter.convert("-"));
        }

        @ParameterizedTest
        @CsvSource({
                "fa, ふぁ", "fi, ふぃ", "fu, ふ", "fe, ふぇ", "fo, ふぉ",
                "va, ゔぁ", "vi, ゔぃ", "vu, ゔ", "ve, ゔぇ", "vo, ゔぉ",
        })
        void shouldConvertFAndV(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }

        @ParameterizedTest
        @CsvSource({
                "tha, てゃ", "thi, てぃ", "thu, てゅ",
                "dha, でゃ", "dhi, でぃ", "dhu, でゅ",
                "tsa, つぁ", "tsi, つぃ", "tse, つぇ", "tso, つぉ",
        })
        void shouldConvertSpecialCombinations(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }

        @ParameterizedTest
        @CsvSource({
                "ca, か", "cu, く", "co, こ", "ci, し", "ce, せ",
        })
        void shouldConvertC(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }

        @ParameterizedTest
        @CsvSource({
                "qa, くぁ", "qi, くぃ", "qu, く", "qe, くぇ", "qo, くぉ",
        })
        void shouldConvertQ(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }

        @ParameterizedTest
        @CsvSource({
                "zh, ←", "zj, ↓", "zk, ↑", "zl, →",
        })
        void shouldConvertArrowShortcuts(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }

        @Test
        void shouldConvertPeriod() {
            assertEquals("。", converter.convert("."));
        }

        @Test
        void shouldConvertComma() {
            assertEquals("、", converter.convert(","));
        }
    }

    // === Minecraftフォーマットコード ===

    @Nested
    class FormattingCodes {
        @ParameterizedTest
        @CsvSource({
                "§0, §0", "§1, §1", "§a, §a", "§f, §f",
                "§k, §k", "§l, §l", "§m, §m", "§n, §n", "§o, §o", "§r, §r",
        })
        void shouldPreserveFormattingCodes(String input, String expected) {
            assertEquals(expected, converter.convert(input));
        }

        @Test
        void shouldPreserveFormattingCodesInContext() {
            assertEquals("§cあか§r", converter.convert("§caka§r"));
        }
    }

    // === 一致しない文字のパススルー ===

    @Nested
    class Passthrough {
        @Test
        void shouldPassthroughUppercase() {
            assertEquals("ABC", converter.convert("ABC"));
        }

        @Test
        void shouldPassthroughNumbers() {
            assertEquals("123", converter.convert("123"));
        }

        @Test
        void shouldPassthroughSpecialChars() {
            assertEquals("@#$%", converter.convert("@#$%"));
        }

        @Test
        void shouldPassthroughSpaces() {
            assertEquals("あ い", converter.convert("a i"));
        }
    }

    // === 空文字列・境界ケース ===

    @Nested
    class EdgeCases {
        @Test
        void shouldHandleEmptyString() {
            assertEquals("", converter.convert(""));
        }

        @Test
        void shouldHandleSingleChar() {
            assertEquals("あ", converter.convert("a"));
        }

        @Test
        void shouldHandleOnlyNonMatchingChars() {
            assertEquals("XYZ", converter.convert("XYZ"));
        }
    }

    // === 実際の文章 ===

    @Nested
    class RealSentences {
        @Test
        void shouldConvertKonnichiwa() {
            assertEquals("こんにちは", converter.convert("konnnichiha"));
        }

        @Test
        void shouldConvertArigatou() {
            assertEquals("ありがとう", converter.convert("arigatou"));
        }

        @Test
        void shouldConvertOhayou() {
            assertEquals("おはよう", converter.convert("ohayou"));
        }

        @Test
        void shouldConvertSakura() {
            assertEquals("さくら", converter.convert("sakura"));
        }

        @Test
        void shouldConvertGakkou() {
            assertEquals("がっこう", converter.convert("gakkou"));
        }

        @Test
        void shouldConvertNippon() {
            assertEquals("にっぽん", converter.convert("nippon"));
        }

        @Test
        void shouldConvertTokyo() {
            assertEquals("とうきょう", converter.convert("toukyou"));
        }

        @Test
        void shouldConvertMixed() {
            // H=passthrough, e=え, ll=っ(backtrack -1), lo=ぉ, ,=、, sekai=せかい, ！=passthrough
            assertEquals("Hえっぉ、せかい！", converter.convert("Hello,sekai！"));
        }
    }

    // === fromResource のエラーハンドリング ===

    @Nested
    class ResourceLoading {
        @Test
        void shouldThrowOnMissingResource() {
            assertThrows(IllegalArgumentException.class,
                    () -> RomajiToHiragana.fromResource("/nonexistent.txt", null));
        }

        @Test
        void shouldLogWarningForInvalidLines() {
            // loggerがnullでも例外にならないことを確認（無効行はスキップされる）
            assertDoesNotThrow(() ->
                    RomajiToHiragana.fromResource(
                            "/assets/japaneseromajiconverter/romaji_to_hiragana.txt", null));
        }
    }

    // === 最長一致の検証 ===

    @Nested
    class LongestMatch {
        @Test
        void shouldPreferLongerMatch_sha_over_s_ha() {
            // "sha" は3文字一致で「しゃ」、"s"+"ha" ではない
            assertEquals("しゃ", converter.convert("sha"));
        }

        @Test
        void shouldPreferLongerMatch_chi_over_c_hi() {
            assertEquals("ち", converter.convert("chi"));
        }

        @Test
        void shouldPreferLongerMatch_tsu_over_t_su() {
            assertEquals("つ", converter.convert("tsu"));
        }

        @Test
        void shouldPreferLongerMatch_ltsu_over_l_tsu() {
            // 4文字一致 ltsu → っ
            assertEquals("っ", converter.convert("ltsu"));
        }

        @Test
        void shouldPreferLongerMatch_hwyu_over_hw_yu() {
            // 4文字一致 hwyu → ふゅ
            assertEquals("ふゅ", converter.convert("hwyu"));
        }
    }
}
