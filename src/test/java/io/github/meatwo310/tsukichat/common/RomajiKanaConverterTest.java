package io.github.meatwo310.tsukichat.common;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RomajiKanaConverterTest {
    @Test
    void getRomajiToKanaMap() {
        var romajiToKanaMap = RomajiKanaConverter.ROMAJI_TO_KANA;
        assertFalse(romajiToKanaMap.isEmpty());
        assertEquals("か", romajiToKanaMap.get("ka"));
    }

    @Test
    void getRomajiRollbackMap() {
        var romajiRollbackMap = RomajiKanaConverter.ROMAJI_ROLLBACK;
        assertFalse(romajiRollbackMap.isEmpty());
        assertEquals(1, romajiRollbackMap.get("tt"));
    }

    @Test
    void convertToKana() {
        Map<String, String> testCases = new HashMap<>();
        testCases.put("wagahaihanekodearu", "わがはいはねこである");
        testCases.put("namaehamadanai", "なまえはまだない");
        testCases.put("dokodeumaretakatontokentougatukanu", "どこでうまれたかとんとけんとうがつかぬ");
        testCases.put("dokodeumaretakatonntokenntougatsukanu", "どこでうまれたかとんとけんとうがつかぬ");
        testCases.put(
                "nandemousuguraijimejimesitatokorodenya-nya-naiteitakotodakehakiokusiteiru",
                "なんでもうすぐらいじめじめしたところでにゃーにゃーないていたことだけはきおくしている"
        );
        testCases.put(
                "nanndemousuguraizimezimeshitatokorodenilya-nilya-naiteitakotodakehakiokushiteiru",
                "なんでもうすぐらいじめじめしたところでにゃーにゃーないていたことだけはきおくしている"
        );
        testCases.put("MINECRAFT", "MINECRAFT");
        testCases.put("Minecraft", "Mいねcらft");

        testCases.forEach((input, expected) -> {
            String result = RomajiKanaConverter.convertToKana(input);
            assertEquals(expected, result);
        });
    }
}
