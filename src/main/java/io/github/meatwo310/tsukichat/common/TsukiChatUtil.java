package io.github.meatwo310.tsukichat.common;

public class TsukiChatUtil {
    public static boolean isClassAvailable(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
