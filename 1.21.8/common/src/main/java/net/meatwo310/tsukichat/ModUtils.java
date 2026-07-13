package net.meatwo310.tsukichat;

import net.minecraft.resources.ResourceLocation;

public final class ModUtils {
    private ModUtils() {}

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MODID, path);
    }
}
