package io.github.gcjojo.liblib.factory;

import io.github.gcjojo.liblib.utils.PlayerSaveData;

public interface PlayerDataFactory<T extends PlayerSaveData> {
    T create();
}
