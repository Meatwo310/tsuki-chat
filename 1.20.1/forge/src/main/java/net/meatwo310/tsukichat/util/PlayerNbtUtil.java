package net.meatwo310.tsukichat.util;

import net.meatwo310.tsukichat.ModMain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

public class PlayerNbtUtil {
    private static final String KEY_NAME = ModMain.MODID;

    /**
     * @return キーがすでに存在していて上書きした場合true
     */
    public static boolean saveCompoundTag(Player player, String name, CompoundTag compoundTag) {
        /*
        {
            "tsukichat": {
                "name": {
                    "key": "value",
                    ...
                }
            }
        }
        */

        CompoundTag playerData = player.getPersistentData();
        CompoundTag tsukichatData = playerData.contains(KEY_NAME) ? playerData.getCompound(KEY_NAME) : new CompoundTag();
        boolean exists = tsukichatData.contains(name);
        CompoundTag keyData = exists ? tsukichatData.getCompound(name) : new CompoundTag();

        compoundTag.getAllKeys().forEach(k -> {
            Tag tag = compoundTag.get(k);
            if (tag != null) keyData.put(k, tag);
        });

        tsukichatData.put(name, keyData);
        playerData.put(KEY_NAME, tsukichatData);
        return exists;
    }


    /**
     * @return キーの削除に成功した場合true
     */
    public static boolean removeTag(Player player, String name, String key) {
        CompoundTag playerData = player.getPersistentData();
        if (!playerData.contains(KEY_NAME)) return false;
        CompoundTag tsukichatData = playerData.getCompound(KEY_NAME);
        if (!tsukichatData.contains(name)) return false;
        CompoundTag keyData = tsukichatData.getCompound(name);
        if (!keyData.contains(key)) return false;

        keyData.remove(key);
        return true;
    }

    /**
     * @return nameの削除に成功した場合true
     */
    public static boolean removeWhole(Player player, String name) {
        CompoundTag playerData = player.getPersistentData();
        if (!playerData.contains(KEY_NAME)) return false;
        CompoundTag tsukichatData = playerData.getCompound(KEY_NAME);
        if (!tsukichatData.contains(name)) return false;

        tsukichatData.remove(name);
        return true;
    }

    public static CompoundTag loadCompoundTag(Player player, String name) {
        CompoundTag playerData = player.getPersistentData();
        if (!playerData.contains(KEY_NAME)) return new CompoundTag();
        CompoundTag tsukichatData = playerData.getCompound(KEY_NAME);

        return tsukichatData.contains(name) ? tsukichatData.getCompound(name) : new CompoundTag();
    }

    public static void clonePlayerData(Player originalPlayer, Player newPlayer) {
        CompoundTag originalPlayerData = originalPlayer.getPersistentData();
        if (!originalPlayerData.contains(KEY_NAME)) return;
        CompoundTag tsukichatData = originalPlayerData.getCompound(KEY_NAME);

        CompoundTag newPlayerData = newPlayer.getPersistentData();
        newPlayerData.put(KEY_NAME, tsukichatData);
    }
}
