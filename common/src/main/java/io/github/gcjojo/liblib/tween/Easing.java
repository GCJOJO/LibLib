package io.github.gcjojo.liblib.tween;

@FunctionalInterface
public interface Easing {
    // https://easings.net/
    Easing LINEAR = (t) -> t;

    float apply(float t);

    interface Back {
        double constant1 = 1.70158;
        double constant2 = constant1 * 1.525;
        Easing EASE_IN_OUT = (x) -> (float) (x < 0.5
                ? (Math.pow(2 * x, 2) * ((constant2 + 1) * 2 * x - constant2)) / 2
                : (Math.pow(2 * x - 2, 2) * ((constant2 + 1) * (x * 2 - 2) + constant2) + 2) / 2);
        static final double constant3 = constant1 + 1;
        Easing EASE_IN = (t) -> (float) (constant3 * t * t * t - constant1 * t * t);
        Easing EASE_OUT = (t) -> (float) (1 + constant3 * Math.pow(t - 1, 3) + constant1 * Math.pow(t - 1, 2));
    }

    interface Sine {
        Easing EASE_IN = (t) -> (float) (1 - Math.cos((t * Math.PI) / 2));
        Easing EASE_OUT = (t) -> (float) Math.sin((t * Math.PI) / 2);
        Easing EASE_IN_OUT = (t) -> (float) (-(Math.cos(Math.PI * t) - 1) / 2);
    }

    interface Cubic {
        Easing EASE_IN = (t) -> t * t * t;
        Easing EASE_OUT = (t) -> 1 - (float) Math.pow(1 - t, 3);
        Easing EASE_IN_OUT = (t) -> t < 0.5 ? 4 * t * t * t : (float) (1 - Math.pow(-2 * t + 2, 3) / 2);
    }

    interface Quad {
        Easing EASE_IN = (t) -> t * t;
        Easing EASE_OUT = (t) -> 1 - (1 - t) * (1 - t);
        Easing EASE_IN_OUT = (t) -> t < 0.5 ? 2 * t * t : (float) (1 - Math.pow(-2 * t + 2, 2) / 2);
    }

    interface Quint {
        Easing EASE_IN = (t) -> t * t * t * t * t;
        Easing EASE_OUT = (t) -> 1 - (float) Math.pow(1 - t, 5);
        Easing EASE_IN_OUT = (t) -> t < 0.5 ? 16 * t * t * t * t * t : 1 - (float) Math.pow(-2 * t + 2, 5) / 2;
    }

    interface Expo {
        Easing EASE_IN = (t) -> t == 0 ? 0 : (float) Math.pow(2, 10 * t - 10);
        Easing EASE_OUT = (t) -> t == 1 ? 1 : 1 - (float) Math.pow(2, -10 * t);
        Easing EASE_IN_OUT = (t) -> t == 0 ? 0 : t == 1 ? 1 : t < 0.5 ? (float) Math.pow(2, 20 * t - 10) / 2 : (2 - (float) Math.pow(2, -20 * t + 10)) / 2;
    }

    interface Circ {
        Easing EASE_IN = (t) -> 1 - (float) Math.sqrt(1 - Math.pow(t, 2));
        Easing EASE_OUT = (t) -> (float) Math.sqrt(1 - Math.pow(t - 1, 2));
        Easing EASE_IN_OUT = (t) -> t < 0.5 ? (float) (1 - Math.sqrt(1 - Math.pow(2 * t, 2))) / 2 : (float) (Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2;

    }

    interface Elastic {
        double constant1 = (2 * Math.PI) / 3;
        double constant2 = (2 * Math.PI) / 4.5;
        Easing EASE_IN = (t) -> t == 0 ? 0 : (float) (t == 1 ? 1 : -Math.pow(2, 10 * t - 10) * Math.sin((t * 10 - 10.75) * constant1));
        Easing EASE_OUT = (t) -> t == 0 ? 0 : (float) (t == 1 ? 1 : Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * constant1) + 1);
        Easing EASE_IN_OUT = (x) -> x == 0 ? 0 : (float) (x == 1 ? 1 : x < 0.5 ? -(Math.pow(2, 20 * x - 10) * Math.sin((20 * x - 11.125) * constant2)) / 2 : (Math.pow(2, -20 * x + 10) * Math.sin((20 * x - 11.125) * constant2)) / 2 + 1);
    }

    interface Bounce {
        double n1 = 7.5625;
        double d1 = 2.75;

        Easing EASE_OUT = (t) -> {
            if (t < 1 / d1) {
                return (float) (n1 * t * t);
            } else if (t < 2 / d1) {
                return (float) (n1 * (t -= (float) (1.5 / d1)) * t + 0.75);
            } else if (t < 2.5 / d1) {
                return (float) (n1 * (t -= (float) (2.25 / d1)) * t + 0.9375);
            } else {
                return (float) (n1 * (t -= (float) (2.625 / d1)) * t + 0.984375);
            }
        };
        Easing EASE_IN = (t) -> 1 - EASE_OUT.apply(1 - t);
        Easing EASE_IN_OUT = (t) -> t < 0.5
                ? (1 - EASE_OUT.apply(1 - 2 * t)) / 2
                : (1 + EASE_OUT.apply(2 * t - 1)) / 2;
    }

}
