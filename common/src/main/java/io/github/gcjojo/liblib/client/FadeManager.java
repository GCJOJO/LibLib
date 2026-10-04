package io.github.gcjojo.liblib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.gcjojo.liblib.math.Color;
import io.github.gcjojo.liblib.tween.Easing;
import io.github.gcjojo.liblib.tween.Interpolator;
import io.github.gcjojo.liblib.tween.TweenManager;
import io.github.gcjojo.liblib.tween.TweenSequence;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Map;
import java.util.TreeMap;

public class FadeManager {

    private static final TreeMap<Integer, FadeState> fadeStates = new TreeMap<Integer, FadeState>();

    private static class FadeState {
        private final int layer;
        @Getter
        private final Color startColor;
        @Getter
        private final Color endColor;
        @Getter
        @Setter
        private Color currentColor;

        TweenSequence fadeSequence;

        public FadeState(int newLayer, Color newStartColor, Color newEndColor, Easing easing, float easeTime)
        {
            layer = newLayer;
            currentColor = startColor = newStartColor;
            endColor = newEndColor;

            fadeSequence = TweenManager.createTweenSequence(TweenManager.TweenSide.CLIENT);
            fadeSequence.setParallel(false);
            fadeSequence.tweenProperty(this::getCurrentColor, this::setCurrentColor, Interpolator.COLOR)
                    .values(startColor, endColor)
                    .easing(easing)
                    .duration(easeTime);

            fadeSequence.play();
        }

        public void stop()
        {
            if(fadeSequence != null && !fadeSequence.isFinished())
                fadeSequence.stop();
        }
    }

    public static void fade(int layer, Color startColor, Color endColor, Easing easing, float easeTime)
    {
        Minecraft.getInstance().execute(() -> fadeStates.put(layer, new FadeState(layer, startColor, endColor, easing, easeTime)));
    }

    public static void renderFade(GuiGraphics graphics)
    {
        if(fadeStates.isEmpty()) return;

        graphics.flush();

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        for (Map.Entry<Integer, FadeState> entry : fadeStates.entrySet()) {
            FadeState state = entry.getValue();
            graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), 1000, state.getCurrentColor().getColorInt());
        }

        graphics.flush();

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    public static void clearFade(int layer)
    {
        Minecraft.getInstance().execute(() -> {
            if(!fadeStates.containsKey(layer))
                return;

            FadeState state = fadeStates.get(layer);
            state.stop();
            fadeStates.remove(layer);
        });
    }

    public static void clearAllFade()
    {
        Minecraft.getInstance().execute(() -> {
            fadeStates.forEach((integer, fadeState) -> fadeState.stop());
            fadeStates.clear();
        });
    }
}
