package io.github.gcjojo.liblib.bbcode;

import io.github.gcjojo.liblib.math.Color;

public interface TextEffect {
    CharTransform apply(int charIndex, float time, Color baseColor);

    record CharTransform(float offsetX, float offsetY, float rotationDegrees, float scale, Color color) {
        public static final CharTransform DEFAULT = new CharTransform(0, 0, 0, 1, Color.WHITE);
    }
}
