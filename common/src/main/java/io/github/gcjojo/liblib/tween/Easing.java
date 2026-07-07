package io.github.gcjojo.liblib.tween;

@FunctionalInterface
public interface Easing {
    static final double constant1 = 1.70158;
    static final double constant2 = constant1 * 1.525;
    Easing EASE_IN_OUT_BACK = (x) -> (float) (x < 0.5
            ? (Math.pow(2 * x, 2) * ((constant2 + 1) * 2 * x - constant2)) / 2
            : (Math.pow(2 * x - 2, 2) * ((constant2 + 1) * (x * 2 - 2) + constant2) + 2) / 2);
    static final double constant3 = constant1 + 1;
    Easing EASE_IN_BACK = (t) -> (float) (constant3 * t * t * t - constant1 * t * t);
    Easing EASE_OUT_BACK = (t) -> (float) (1 + constant3 * Math.pow(t - 1, 3) + constant1 * Math.pow(t - 1, 2));
    // https://easings.net/
    Easing LINEAR = (t) -> t;
    Easing EASE_IN_SINE = (t) -> (float) (1 - Math.cos((t * Math.PI) / 2));
    Easing EASE_OUT_SINE = (t) -> (float) Math.sin((t * Math.PI) / 2);
    Easing EASE_IN_OUT_SINE = (t) -> (float) (-(Math.cos(Math.PI * t) - 1) / 2);
    Easing EASE_IN_CUBIC = (t) -> t * t * t;
    Easing EASE_OUT_CUBIC = (t) -> 1 - (float) Math.pow(1 - t, 3);
    Easing EASE_IN_OUT_CUBIC = (t) -> t < 0.5 ? 4 * t * t * t : (float) (1 - Math.pow(-2 * t + 2, 3) / 2);
    Easing EASE_IN_QUAD = (t) -> t * t;
    Easing EASE_OUT_QUAD = (t) -> 1 - (1 - t) * (1 - t);
    Easing EASE_IN_OUT_QUAD = (t) -> t < 0.5 ? 2 * t * t : (float) (1 - Math.pow(-2 * t + 2, 2) / 2);
    Easing EASE_IN_QUINT = (t) -> t * t * t * t * t;
    Easing EASE_OUT_QUINT = (t) -> 1 - (float) Math.pow(1 - t, 5);
    Easing EASE_IN_OUT_QUINT = (t) -> t < 0.5 ? 16 * t * t * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 5) / 2;
    Easing EASE_IN_EXPO = (t) -> t == 0 ? 0 : (float) Math.pow(2, 10 * t - 10);
    Easing EASE_OUT_EXPO = (t) -> t == 1 ? 1 : 1 - (float) Math.pow(2, -10 * t);
    Easing EASE_IN_OUT_EXPO = (t) -> t == 0 ? 0 : t == 1 ? 1 : t < 0.5 ? (float) Math.pow(2, 20 * t - 10) / 2 : (2 - (float) Math.pow(2, -20 * t + 10)) / 2;
    Easing EASE_IN_CIRC = (t) -> 1 - (float) Math.sqrt(1 - Math.pow(t, 2));
    Easing EASE_OUT_CIRC = (t) -> (float) Math.sqrt(1 - Math.pow(t - 1, 2));
    Easing EASE_IN_OUT_CIRC = (t) -> t < 0.5 ? (float) (1 - Math.sqrt(1 - Math.pow(2 * t, 2))) / 2 : (float) (Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2;

    // TODO Ease Elastic and Bounce

    float apply(float t);
}
