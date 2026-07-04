package io.github.gcjojo.liblib;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.PlayerEvent;
import io.github.gcjojo.liblib.client.SoundPlayer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

import java.util.Arrays;

public final class LibLib {
    public static final String MOD_ID = "liblib";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static SoundPlayer SOUND_PLAYER;
    private static PlayerDataManager PLAYER_DATA_MANAGER;

    public static void init() {
        PlayerEvent.PLAYER_CLONE.register((ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wonGame) -> {
            PLAYER_DATA_MANAGER.copyPlayer(oldPlayer, newPlayer);
        });
    }

    public static SoundPlayer getSoundPlayer() {
        return SOUND_PLAYER;
    }

    public static void setSoundPlayer(SoundPlayer newSoundPlayer) {
        SOUND_PLAYER = newSoundPlayer;
    }

    public static PlayerDataManager getPlayerDataManager() {
        return PLAYER_DATA_MANAGER;
    }

    public static void setPlayerDataManager(PlayerDataManager newPlayerDataManager) {
        PLAYER_DATA_MANAGER = newPlayerDataManager;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void printException(String message, Throwable e) {
        getLogger().error("{}\nError : {}", message, e.toString());
        Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> getLogger().error(stackTraceElement.toString()));
    }
}
