package io.github.gcjojo.factory;

import io.github.gcjojo.liblib.utils.PlayerData;

import java.util.HashMap;
import java.util.Map;

public class PlayerDataRegistry {
    private static final Map<Class<? extends PlayerData>, PlayerDataFactory<?>> FACTORIES = new HashMap<>();

    public static <T extends PlayerData> void register(Class<T> playerDataClass, PlayerDataFactory<T> factory) {
        FACTORIES.put(playerDataClass, factory);
    }

    @SuppressWarnings("unchecked")
    public static <T extends PlayerData> T create(Class<T> playerDataClass) {
        PlayerDataFactory<T> factory = (PlayerDataFactory<T>) FACTORIES.get(playerDataClass);
        if (factory == null) {
            throw new IllegalStateException("No factory found for " + playerDataClass.toString());
        }
        return factory.create();
    }
}
