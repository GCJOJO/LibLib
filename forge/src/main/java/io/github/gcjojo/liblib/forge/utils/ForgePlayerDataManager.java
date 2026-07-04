package io.github.gcjojo.liblib.forge.utils;

import io.github.gcjojo.liblib.utils.PlayerDataManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class ForgePlayerDataManager extends PlayerDataManager {
    private CompoundTag getBlablalibTag(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains("LibLib"))
            persistentData.put("LibLib", new CompoundTag());
        return persistentData.getCompound("LibLib");
    }

    @Override
    public CompoundTag getAdditionalData(Player player) {
        CompoundTag liblibTag = getBlablalibTag(player);
        if (!liblibTag.contains("AdditionalData"))
            liblibTag.put("AdditionalData", new CompoundTag());
        return liblibTag.getCompound("AdditionalData");
    }

    @Override
    public void setAdditionalData(Player player, CompoundTag data) {
        getAdditionalData(player).merge(data);
    }
}
