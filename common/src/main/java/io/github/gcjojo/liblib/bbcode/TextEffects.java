package io.github.gcjojo.liblib.bbcode;

import io.github.gcjojo.liblib.math.Color;
import io.github.gcjojo.liblib.utils.MathUtils;

import java.util.List;
import java.util.Random;

// Full ClaudeSlop
public class TextEffects {
    // [shake] : tremblement aléatoire
    public static TextEffect shake(float intensity, float speed) {
        return (charIndex, time, baseColor) -> {
            // Seed basé sur charIndex + time discrétisé, pour un tremblement qui change dans le temps
            long seed = charIndex * 1000L + (long) (time * speed * 20);
            Random random = new Random(seed);
            float offsetX = (random.nextFloat() - 0.5f) * intensity;
            float offsetY = (random.nextFloat() - 0.5f) * intensity;
            return new TextEffect.CharTransform(offsetX, offsetY, 0, 1, baseColor);
        };
    }

    // [wave] : ondulation sinusoïdale verticale, décalée par caractère
    public static TextEffect wave(float amplitude, float frequency, float speed) {
        return (charIndex, time, baseColor) -> {
            float phase = charIndex * frequency;
            float offsetY = (float) Math.sin(time * speed + phase) * amplitude;
            return new TextEffect.CharTransform(0, offsetY, 0, 1, baseColor);
        };
    }

    // [tornado] : rotation + déplacement circulaire
    public static TextEffect tornado(float radius, float speed) {
        return (charIndex, time, baseColor) -> {
            float phase = charIndex * 0.5f;
            float angle = time * speed + phase;
            float offsetX = (float) Math.cos(angle) * radius;
            float offsetY = (float) Math.sin(angle) * radius;
            float rotation = (float) Math.toDegrees(angle) % 360f;
            return new TextEffect.CharTransform(offsetX, offsetY, rotation, 1, baseColor);
        };
    }

    // [rainbow] : cycle de teinte HSB, décalé par caractère
    public static TextEffect rainbow(float speed, float saturation, float brightness) {
        return (charIndex, time, baseColor) -> {
            float hue = ((time * speed) + (charIndex * 0.1f)) % 1.0f;
            int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
            // TODO Fix conversion I know it's wrong
            return new TextEffect.CharTransform(0, 0, 0, 1, new Color(rgb));
        };
    }

    // [gradient=color1,color2] : dégradé statique le long du texte (pas animé, dépend juste de la position)
    public static TextEffect gradient(Color colorStart, Color colorEnd, int spanLength) {
        if (colorStart == null || colorEnd == null)
            return (charIndex, time, baseColor) -> new TextEffect.CharTransform(0, 0, 0, 1, Color.WHITE);

        // On utilise un compteur interne car le charIndex global ne correspond pas à la position dans le span
        int[] localCounter = {0};

        return (charIndex, time, baseColor) -> {
            float t = spanLength <= 1 ? 0 : (float) localCounter[0] / (spanLength - 1);
            localCounter[0]++;
            Color color = MathUtils.lerp(colorStart, colorEnd, t);
            return new TextEffect.CharTransform(0, 0, 0, 1, color);
        };
    }

    public static TextEffect pulse(float speed, float minScale, float maxScale) {
        return (charIndex, time, baseColor) -> {
            float scale = minScale + (maxScale - minScale) * (0.5f + 0.5f * (float) Math.sin(time * speed));
            return new TextEffect.CharTransform(0, 0, 0, scale, baseColor);
        };
    }

    public static TextEffect.CharTransform combine(List<TextEffect> effects, int charIndex, float time, Color baseColor) {
        float totalOffsetX = 0, totalOffsetY = 0, totalRotation = 0, totalScale = 1;
        Color finalColor = baseColor;

        for (TextEffect effect : effects) {
            TextEffect.CharTransform t = effect.apply(charIndex, time, finalColor);
            totalOffsetX += t.offsetX();
            totalOffsetY += t.offsetY();
            totalRotation += t.rotationDegrees();
            totalScale *= t.scale();
            finalColor = t.color();
        }

        return new TextEffect.CharTransform(totalOffsetX, totalOffsetY, totalRotation, totalScale, finalColor);
    }
}
