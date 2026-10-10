package io.github.gcjojo.liblib.tween;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.LinkedHashMap;
import java.util.Map;

// Claude L + get good Slop
public final class EasingCodec {
    private static final Map<String, Easing> BY_NAME = new LinkedHashMap<>();
    private static final Map<Easing, String> BY_VALUE = new LinkedHashMap<>();

    public static void register(String name, Easing easing) {
        if(BY_NAME.putIfAbsent(name, easing) != null)
            throw new IllegalArgumentException("Easing already registered : " + name);
        BY_VALUE.put(easing, name);
    }

    public static final Codec<Easing> CODEC = Codec.STRING.flatXmap(
            name -> {
                Easing e = BY_NAME.get(name);
                return e != null ? DataResult.success(e)
                        : DataResult.error(() -> "Unknown Easing : " + name);
            },
            easing -> {
                String name = BY_VALUE.get(easing);
                return name != null ? DataResult.success(name)
                        : DataResult.error(() -> "Unregistered Easing, cannot serialize");
            });

    static {
        register("linear", Easing.LINEAR);

        register("sine_in", Easing.Sine.EASE_IN);
        register("sine_out", Easing.Sine.EASE_OUT);
        register("sine_in_out", Easing.Sine.EASE_IN_OUT);

        register("quad_in", Easing.Quad.EASE_IN);
        register("quad_out", Easing.Quad.EASE_OUT);
        register("quad_in_out", Easing.Quad.EASE_IN_OUT);

        register("cubic_in", Easing.Cubic.EASE_IN);
        register("cubic_out", Easing.Cubic.EASE_OUT);
        register("cubic_in_out", Easing.Cubic.EASE_IN_OUT);

        register("quint_in", Easing.Quint.EASE_IN);
        register("quint_out", Easing.Quint.EASE_OUT);
        register("quint_in_out", Easing.Quint.EASE_IN_OUT);

        register("expo_in", Easing.Expo.EASE_IN);
        register("expo_out", Easing.Expo.EASE_OUT);
        register("expo_in_out", Easing.Expo.EASE_IN_OUT);

        register("circ_in", Easing.Circ.EASE_IN);
        register("circ_out", Easing.Circ.EASE_OUT);
        register("circ_in_out", Easing.Circ.EASE_IN_OUT);

        register("back_in", Easing.Back.EASE_IN);
        register("back_out", Easing.Back.EASE_OUT);
        register("back_in_out", Easing.Back.EASE_IN_OUT);

        register("elastic_in", Easing.Elastic.EASE_IN);
        register("elastic_out", Easing.Elastic.EASE_OUT);
        register("elastic_in_out", Easing.Elastic.EASE_IN_OUT);

        register("bounce_in", Easing.Bounce.EASE_IN);
        register("bounce_out", Easing.Bounce.EASE_OUT);
        register("bounce_in_out", Easing.Bounce.EASE_IN_OUT);
    }

    private EasingCodec() {}
}
