package io.github.gcjojo.liblib;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.github.gcjojo.liblib.api.BlablaLibAPI;
import io.github.gcjojo.liblib.api.QuestsLibAPI;
import io.github.gcjojo.liblib.client.SoundPlayer;
import io.github.gcjojo.liblib.client.gui.TestGui;
import io.github.gcjojo.liblib.client.gui.elements.GuiElement;
import io.github.gcjojo.liblib.events.LibLibEvents;
import io.github.gcjojo.liblib.factory.PlayerDataRegistry;
import io.github.gcjojo.liblib.tween.TweenManager;
import io.github.gcjojo.liblib.utils.PlayerDataManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Arrays;

public final class LibLib {
    public static final String MOD_ID = "liblib";
    public static final KeyMapping TEST_SCREEN_KEY = new KeyMapping(
            String.format("key.%s.open_test_screen", LibLib.MOD_ID),
            GLFW.GLFW_KEY_K,
            String.format("key.categories.%s", LibLib.MOD_ID)
    );
    public static final KeyMapping TOGGLE_GUI_PIVOT_KEY = new KeyMapping(
            String.format("key.%s.toggle_gui_pivot_drawing", LibLib.MOD_ID),
            GLFW.GLFW_KEY_O,
            String.format("key.categories.%s", LibLib.MOD_ID)
    );
    public static final KeyMapping TOGGLE_GUI_BOUNDING_BOX_KEY = new KeyMapping(
            String.format("key.%s.toggle_gui_bounding_box_drawing", LibLib.MOD_ID),
            GLFW.GLFW_KEY_L,
            String.format("key.categories.%s", LibLib.MOD_ID)
    );
    public static final KeyMapping TOGGLE_GUI_SCISSORS_KEY = new KeyMapping(
            String.format("key.%s.toggle_gui_scissors_drawing", LibLib.MOD_ID),
            GLFW.GLFW_KEY_M,
            String.format("key.categories.%s", LibLib.MOD_ID)
    );

    private static final Logger LOGGER = LogUtils.getLogger();
    private static long lastTick = -1;
    private static SoundPlayer SOUND_PLAYER;
    private static PlayerDataManager PLAYER_DATA_MANAGER;

    private static BlablaLibAPI BLABLALIB_API = new BlablaLibAPI.EmptyBlablaLibAPI();
    private static QuestsLibAPI QUESTSLIB_API = new QuestsLibAPI.EmptyQuestsLibAPI();

    public static void init() {
        LibLibEventManager.registerEvents();
        PlayerDataRegistry.register(PlayerInventorySaveData.class, PlayerInventorySaveData::new);

        TickEvent.ServerLevelTick.SERVER_PRE.register((server) -> {
            if (lastTick == -1) {
                lastTick = System.nanoTime();
                return;
            }

            long now = System.nanoTime();
            // Merci claude slop pour la valeur marrante
            float deltaTime = (float) (now - lastTick) / 1_000_000_000.0f;
            lastTick = now;

            TweenManager.updateSequences(deltaTime, TweenManager.TweenSide.SERVER);
        });

        LibLibEvents.PLAYER_INVENTORY_CHANGED.register((player, inventoryDifference) -> {
            getLogger().info("{}'s inventory has changed :", player.getName().getString());
            inventoryDifference.forEach((itemId, amountDelta) -> getLogger().info("    {}x{}", amountDelta, itemId));
        });
    }

    public static void initClient() {
        KeyMappingRegistry.register(TEST_SCREEN_KEY);
        KeyMappingRegistry.register(TOGGLE_GUI_PIVOT_KEY);
        KeyMappingRegistry.register(TOGGLE_GUI_BOUNDING_BOX_KEY);
        KeyMappingRegistry.register(TOGGLE_GUI_SCISSORS_KEY);

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (TEST_SCREEN_KEY.consumeClick()) {
                minecraft.setScreen(new TestGui(Component.literal("Test GUI")));
            }
            while (TOGGLE_GUI_PIVOT_KEY.consumeClick())
                GuiElement.DEBUG_DRAW_PIVOT_POINT = !GuiElement.DEBUG_DRAW_PIVOT_POINT;
            while (TOGGLE_GUI_BOUNDING_BOX_KEY.consumeClick())
                GuiElement.DEBUG_DRAW_BOUNDING_BOX = !GuiElement.DEBUG_DRAW_BOUNDING_BOX;
            while (TOGGLE_GUI_SCISSORS_KEY.consumeClick())
                GuiElement.DEBUG_DRAW_SCISSORS = !GuiElement.DEBUG_DRAW_SCISSORS;
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

    public static BlablaLibAPI getBlablaLibAPI() {
        return BLABLALIB_API;
    }

    public static void setBlablaLibAPI(BlablaLibAPI api) {
        BLABLALIB_API = api;
    }

    public static QuestsLibAPI getQuestsLibAPI() {
        return QUESTSLIB_API;
    }

    public static void setQuestsLibAPI(QuestsLibAPI api) {
        QUESTSLIB_API = api;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void printException(String message, Throwable e) {
        getLogger().error("{}\nError : {}", message, e.toString());
        Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> getLogger().error(stackTraceElement.toString()));
    }
}
