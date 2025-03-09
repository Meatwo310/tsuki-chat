package io.github.meatwo310.tsukichat.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TsukiChatUtilTest {
    @Test
    void testIsClassAvailable() {
        // Test with a class that exists
        assertTrue(TsukiChatUtil.isClassAvailable("java.lang.String"));

        // Test with a class that does not exist
        assertFalse(TsukiChatUtil.isClassAvailable("com.example.NonExistentClass"));
    }
}