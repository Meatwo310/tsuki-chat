package io.github.meatwo310.tsukichat.compat.mohist;

import io.github.meatwo310.tsukichat.config.CommonConfigs;

public class MohistHelper {
    private static final boolean mohistLoaded;
    public static boolean compatPluginLoaded = false;

    static {
        boolean loaded;
        try {
            Class.forName("com.mohistmc.MohistMC");
            loaded = true;
        } catch (ClassNotFoundException e) {
            loaded = false;
        }
        mohistLoaded = loaded;
    }

    public static boolean isMohistCompatEnabled() {
        return CommonConfigs.mohistCompat.get();
    }

    public static boolean isMohistLoaded() {
        return mohistLoaded;
    }

    public static boolean isCompatPluginLoaded() {
//        try {
//            Class.forName("io.github.meatwo310.tsukichat.compat.mohist.TsukiChatPlugin");
//            return true;
//        } catch (ClassNotFoundException e) {
//            return false;
//        }
        return compatPluginLoaded;
    }
}
