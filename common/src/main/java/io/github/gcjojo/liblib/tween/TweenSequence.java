package io.github.gcjojo.liblib.tween;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
public class TweenSequence {
    final List<Tween> tweens = new ArrayList<>();
    final List<Tween> runningTweens = new ArrayList<>();
    int currentTweenIndex = -1;

    boolean playing = false;
    boolean finished = false;

    public <T> TweenProperty<T> tweenProperty(Supplier<T> getter, Consumer<T> setter, Interpolator<T> interpolator) {
        TweenProperty<T> tweenProperty = TweenProperty.of(getter, setter, interpolator);
        tweens.add(tweenProperty);
        return tweenProperty;
    }

    /**
     * Set if the tweens running after will execute in parallel
     * If set to true prior to calling this method with another true value,
     * will group the tweens prior to this call and execute them in parallel but not the one coming after
     */
    public void setParallel(boolean parallel) {
        TweenParallel tweenParallel = TweenParallel.of(parallel);
        tweens.add(tweenParallel);
    }

    public TweenSequence play() {
        playing = true;
        if (currentTweenIndex == -1)
            nextTween();
        return this;
    }

    public TweenSequence pause() {
        playing = false;
        return this;
    }

    public boolean update(float delta) {
        if (!playing) return false;
        if (finished) return true;

        AtomicBoolean finishedAllRunningTweens = new AtomicBoolean(true);
        runningTweens.forEach(tween -> {
            if (!tween.update(delta))
                finishedAllRunningTweens.set(false);
        });

        if (!finishedAllRunningTweens.get()) return finished;

        if (currentTweenIndex + 1 >= tweens.size())
            finished = true;
        else
            nextTween();
        return finished;
    }

    public void nextTween() {
        runningTweens.clear();
        currentTweenIndex++;
        Tween nextTween = tweens.get(currentTweenIndex);
        if (nextTween instanceof TweenParallel tweenParallel) {
            if (tweenParallel.isParallel) {
                for (int i = currentTweenIndex; i <= tweens.size() - 2; i++) {
                    currentTweenIndex++;
                    nextTween = tweens.get(currentTweenIndex);
                    if (nextTween instanceof TweenParallel)
                        break;
                    runningTweens.add(nextTween);
                }
                return;
            }
            nextTween();
            return;
        }
        runningTweens.add(nextTween);
    }
}
