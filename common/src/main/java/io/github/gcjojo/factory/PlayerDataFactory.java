package io.github.gcjojo.factory;

import io.github.gcjojo.liblib.utils.PlayerSaveData;

public interface PlayerDataFactory<T extends PlayerSaveData> {
    T create();
}
