package io.github.meatwo310.tsukichat.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}