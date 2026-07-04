package io.github.gcjojo.factory;

import io.github.gcjojo.liblib.utils.PlayerData;

public interface PlayerDataFactory<T extends PlayerData> {
    T create();
}
