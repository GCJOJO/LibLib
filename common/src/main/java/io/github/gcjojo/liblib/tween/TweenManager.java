package io.github.gcjojo.liblib.tween;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TweenManager {
    public static Map<Tween<?>, TweenSide> runningTweens = new HashMap<>();

    public static <T> Tween<T> createTween(TweenSide side, Supplier<T> getter, Consumer<T> setter, Interpolator<T> interpolator) {
        Tween<T> newTween = Tween.of(getter, setter, interpolator);
        runningTweens.put(newTween, side);
        return newTween;
    }

    public static void updateTweens(float delta, TweenSide side) {
        runningTweens.forEach((tween, tweenSide) -> {
            if (side == tweenSide)
                tween.update(delta);
        });

        runningTweens.entrySet().removeIf((entry) -> entry.getKey().isFinished());
    }

    public enum TweenSide {
        SERVER,
        CLIENT,
    }
}
