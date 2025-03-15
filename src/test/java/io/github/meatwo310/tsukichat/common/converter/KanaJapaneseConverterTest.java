package io.github.meatwo310.tsukichat.common.converter;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KanaJapaneseConverterTest {
    @Test
    void convertToJapanese() {
        Map<String, String> testCases = new HashMap<>();
        testCases.put("わがはいはねこである", "吾輩は猫である");

        testCases.forEach((input, expected) -> {
            String result = KanaJapaneseConverter.convertToJapanese(input);
            assertEquals(expected, result);
        });
    }
}