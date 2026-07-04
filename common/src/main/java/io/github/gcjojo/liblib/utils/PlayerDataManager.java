package io.github.gcjojo.liblib.utils;

import io.github.gcjojo.factory.PlayerDataRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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

    public void serializePlayerData(Player player, PlayerData data, ResourceLocation dataLocation) {
        getAdditionalData(player, dataLocation.getNamespace()).put(dataLocation.getPath(), data.serialize());
    }

    public <T extends PlayerData> T deserializePlayerData(Player player, ResourceLocation dataLocation, Class<T> playerDataClass) {
        T playerData = PlayerDataRegistry.create(playerDataClass);
        CompoundTag modData = getAdditionalData(player, dataLocation.getNamespace());
        if (!modData.contains(dataLocation.getPath())) modData.put(dataLocation.getPath(), new CompoundTag());

        playerData.deserialize(modData.getCompound(dataLocation.getPath()));

        return playerData;
    }
}
