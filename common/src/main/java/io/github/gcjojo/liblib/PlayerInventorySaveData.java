package io.github.gcjojo.liblib;

import io.github.gcjojo.liblib.utils.PlayerSaveData;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PlayerInventorySaveData extends PlayerSaveData {
    public Map<ResourceLocation, Integer> playerInventory = new HashMap<>();

    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        playerInventory.forEach((itemId, itemAmount) -> {
            nbt.putInt(itemId.toString(), itemAmount);
        });
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        playerInventory.clear();
        nbt.getAllKeys().forEach(itemIdString -> {
            ResourceLocation itemId = ResourceLocation.tryParse(itemIdString);
            playerInventory.put(itemId, nbt.getInt(itemIdString));
        });
    }
}
