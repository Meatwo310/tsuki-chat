package io.github.meatwo310.tsukichat.common;

import io.github.meatwo310.tsukichat.TsukiChat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import static org.junit.jupiter.api.Assertions.*;

class ConverterTest {
    @Test
    @EnabledIf("isTsukiChatClassAvailable")
    void checkModId() {
        assertEquals(TsukiChat.MODID, Converter.MODID);
    }

    // TsukiChatクラスが存在するかどうかを確認する静的メソッド
    static boolean isTsukiChatClassAvailable() {
        return TsukiChatUtil.isClassAvailable("io.github.meatwo310.tsukichat.TsukiChat");
    }
}