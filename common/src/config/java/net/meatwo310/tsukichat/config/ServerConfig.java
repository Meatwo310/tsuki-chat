package net.meatwo310.tsukichat.config;

import net.meatwo310.tsukichat.mdk.config.ConfigEntries;
import net.meatwo310.tsukichat.mdk.config.ConfigEntryBuilder;

public class ServerConfig {
    private static final ConfigEntryBuilder BUILDER = new ConfigEntryBuilder();

    public static final ConfigEntries ENTRIES = BUILDER.build();
}
