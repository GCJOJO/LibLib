package io.github.gcjojo.liblib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public abstract class PlayerDataManager {
    protected abstract CompoundTag getAdditionalData(Player player);

    protected abstract void setAdditionalData(Player player, CompoundTag data);

    public CompoundTag getAdditionalData(Player player, String modId) {
        if (!getAdditionalData(player).contains(modId))
            getAdditionalData(player).put(modId, new CompoundTag());
        return getAdditionalData(player).getCompound(modId);
    }

    public void setAdditionalData(Player player, String modId, CompoundTag data) {
        getAdditionalData(player, modId).merge(data);
    }

    public void copyPlayer(Player oldPlayer, Player newPlayer) {
        setAdditionalData(newPlayer, getAdditionalData(oldPlayer));
    }
}
