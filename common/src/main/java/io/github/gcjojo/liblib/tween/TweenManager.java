package io.github.gcjojo.liblib.tween;

import java.util.HashMap;
import java.util.Map;

public class TweenManager {
    public static Map<TweenSequence, TweenSide> runningSequences = new HashMap<>();

    public static TweenSequence createTweenSequence(TweenSide side) {
        TweenSequence sequence = new TweenSequence();
        runningSequences.put(sequence, side);
        return sequence;
    }

    public static void updateSequences(float delta, TweenSide side) {
        runningSequences.forEach((sequence, tweenSide) -> {
            if (side == tweenSide)
                sequence.update(delta);
        });

        runningSequences.entrySet().removeIf((entry) -> entry.getKey().isFinished());
    }

    public enum TweenSide {
        SERVER,
        CLIENT,
    }
}
