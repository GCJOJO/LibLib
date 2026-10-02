package io.github.gcjojo.liblib;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import io.github.gcjojo.liblib.api.BlablaLibAPI;
import io.github.gcjojo.liblib.api.QuestsLibAPI;
import io.github.gcjojo.liblib.client.CustomCameraManager;
import io.github.gcjojo.liblib.client.SoundPlayer;
import io.github.gcjojo.liblib.client.gui.TestGui;
import io.github.gcjojo.liblib.client.gui.elements.GuiElement;
import io.github.gcjojo.liblib.events.LibLibEvents;
import io.github.gcjojo.liblib.factory.PlayerDataRegistry;
import io.github.gcjojo.liblib.tween.Easing;
import io.github.gcjojo.liblib.tween.Interpolator;
import io.github.gcjojo.liblib.tween.TweenManager;
import io.github.gcjojo.liblib.tween.TweenSequence;
import io.github.gcjojo.liblib.utils.PlayerDataManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
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

    public static final KeyMapping TEST_CAMERA_KEY = new KeyMapping(
            String.format("key.%s.test_camera", LibLib.MOD_ID),
            GLFW.GLFW_KEY_I,
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
        KeyMappingRegistry.register(TEST_CAMERA_KEY);

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
            while (TEST_CAMERA_KEY.consumeClick()) {
                if (minecraft.cameraEntity == null) return;

                CustomCameraManager.setActive(true);

                Vec3 startPos = minecraft.player.getEyePosition();
                Vector3f startRot = new Vector3f(minecraft.player.getYRot(), minecraft.player.getXRot(), 0.0f);

                CustomCameraManager.setPosition(startPos);
                CustomCameraManager.setRotation(startRot);

                // 3. Définir les valeurs cibles (ex: prise de hauteur de 2 blocs + rotation de 30° sur le roll/axe Z)
                Vec3 targetPos = startPos.add(0, 2.0, 0);
                Vector3f targetRot = new Vector3f(startRot.x + 45.0f, startRot.y + 15.0f, 30.0f); // Yaw, Pitch, Roll

                // 4. Créer la séquence de Tween
                TweenSequence sequence = TweenManager.createTweenSequence(TweenManager.TweenSide.CLIENT);

                // Animation parallèle de la Position et de la Rotation
                sequence.setParallel(true);

                // Tween de la Position (Vec3)
                sequence.tweenProperty(CustomCameraManager::getPosition, CustomCameraManager::setPosition, Interpolator.VEC3)
                        .values(startPos, targetPos)
                        .duration(2.0f)
                        .easing(Easing.Cubic.EASE_IN_OUT);

                // Tween de la Rotation (Vector3f)
                sequence.tweenProperty(CustomCameraManager::getRotation, CustomCameraManager::setRotation, Interpolator.VECTOR3F)
                        .values(startRot, targetRot)
                        .duration(2.0f)
                        .easing(Easing.Cubic.EASE_IN_OUT);

                // Fin du groupe parallèle
                sequence.setParallel(false);

                // Pause de 1 seconde au sommet
                sequence.tweenWait(1.0f);

                // Callback de fin pour rétablir la caméra joueur
                sequence.tweenCallback(() -> {
                    LibLib.getLogger().info("Cinématique de caméra terminée !");
                    CustomCameraManager.setActive(false);
                });

                // Lancer la séquence
                sequence.play();
            }
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
