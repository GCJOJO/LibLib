package io.github.gcjojo.liblib.fabric;

import io.github.gcjojo.liblib.PlayerDataManager;
import io.github.gcjojo.liblib.fabric.data_components.ModComponents;
import io.github.gcjojo.liblib.fabric.data_components.PlayerDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public class FabricPlayerDataManager extends PlayerDataManager {
    private PlayerDataComponent getPlayerDataComponent(Player player) {
        return ModComponents.PLAYER_DATA.get(player);
    }

    @Override
    public CompoundTag getAdditionalData(Player player) {
        return getPlayerDataComponent(player).getAdditionalData();
    }

    @Override
    public void setAdditionalData(Player player, CompoundTag data) {
        getAdditionalData(player).merge(data);
    }
}
