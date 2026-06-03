package net.meatwo310.tsukichat.config;

import net.meatwo310.tsukichat.mdk.config.ConfigDeclaration;
import net.meatwo310.tsukichat.mdk.config.ConfigSide;

import java.util.List;

public final class ModConfigs {
    public static final ConfigDeclaration SERVER = ConfigDeclaration.of(ConfigSide.SERVER, ServerConfig.ENTRIES);

    public static final List<ConfigDeclaration> ALL = List.of(SERVER);

    private ModConfigs() {}
}
