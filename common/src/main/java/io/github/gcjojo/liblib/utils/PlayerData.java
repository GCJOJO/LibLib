package io.github.gcjojo.liblib.utils;

import net.minecraft.nbt.CompoundTag;

public abstract class PlayerData {
    public abstract CompoundTag serialize();

    public abstract void deserialize(CompoundTag data);
}
