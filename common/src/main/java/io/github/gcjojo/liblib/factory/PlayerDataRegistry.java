package io.github.gcjojo.liblib.factory;

import io.github.gcjojo.liblib.utils.PlayerSaveData;

import java.util.HashMap;
import java.util.Map;

public class PlayerDataRegistry {
    private static final Map<Class<? extends PlayerSaveData>, PlayerDataFactory<?>> FACTORIES = new HashMap<>();

    public static <T extends PlayerSaveData> void register(Class<T> playerDataClass, PlayerDataFactory<T> factory) {
        FACTORIES.put(playerDataClass, factory);
    }

    @SuppressWarnings("unchecked")
    public static <T extends PlayerSaveData> T create(Class<T> playerDataClass) {
        PlayerDataFactory<T> factory = (PlayerDataFactory<T>) FACTORIES.get(playerDataClass);
        if (factory == null) {
            throw new IllegalStateException("No factory found for " + playerDataClass.toString());
        }
        return factory.create();
    }
}
