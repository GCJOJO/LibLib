package io.github.gcjojo.liblib.utils;

import net.minecraft.nbt.CompoundTag;

public abstract class PlayerSaveData {
    public abstract CompoundTag serialize();

    public abstract void deserialize(CompoundTag nbt);
}
