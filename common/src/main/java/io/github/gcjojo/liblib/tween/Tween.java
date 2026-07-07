package io.github.gcjojo.liblib.tween;

import io.github.gcjojo.liblib.utils.MathUtils;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

//Merci ClaudeSlop
@Getter
public class Tween<T> {
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final Interpolator<T> interpolator;

    private Optional<T> startValue = Optional.empty();
    private Optional<T> endValue = Optional.empty();
    private float duration = 0.0f;
    private Easing easing = Easing.LINEAR;

    private boolean playing = false;
    private float elapsed = 0f;
    private boolean finished = false;
    private Runnable onFinishCallback;

    public Tween(Supplier<T> getter, Consumer<T> setter, Interpolator<T> interpolator) {
        this.getter = getter;
        this.setter = setter;
        this.interpolator = interpolator;
    }

    public static <T> Tween<T> of(Supplier<T> getter, Consumer<T> setter, Interpolator<T> interpolator) {
        return new Tween<T>(getter, setter, interpolator);
    }

    public Tween<T> values(T startValue, T endValue) {
        this.startValue = Optional.ofNullable(startValue);
        this.endValue = Optional.ofNullable(endValue);
        return this;
    }

    public Tween<T> duration(float duration) {
        this.duration = duration * 20.0f; // Transform in seconds
        return this;
    }

    public Tween<T> easing(Easing easing) {
        this.easing = easing;
        return this;
    }

    public Tween<T> onFinished(Runnable onFinishCallback) {
        this.onFinishCallback = onFinishCallback;
        return this;
    }

    public Tween<T> play() {
        playing = true;
        return this;
    }

    public Tween<T> pause() {
        playing = false;
        return this;
    }

    public boolean isValid() {
        return startValue.isPresent() && endValue.isPresent() && duration > 0;
    }

    public boolean update(float delta) {
        if (!playing) return false;
        if (finished) return true;

        elapsed += delta;
        float progress = MathUtils.clamp(elapsed / duration, 0.0f, 1.0f);
        float easedProgress = easing.apply(progress);

        if (isValid())
            setter.accept(interpolator.lerp(startValue.get(), endValue.get(), easedProgress));

        if (progress >= 1.0f) {
            finished = true;
            if (onFinishCallback != null) onFinishCallback.run();
        }
        return finished;
    }
}
