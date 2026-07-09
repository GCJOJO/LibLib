package io.github.gcjojo.liblib.tween;

import io.github.gcjojo.liblib.utils.MathUtils;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

//Merci ClaudeSlop
@Getter
public class TweenProperty<T> extends Tween {
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final Interpolator<T> interpolator;

    private Optional<T> startValue = Optional.empty();
    private Optional<T> endValue = Optional.empty();
    private float duration = 0.0f;
    private Easing easing = Easing.LINEAR;

    private float elapsed = 0f;
    private boolean finished = false;

    private TweenProperty(Supplier<T> getter, Consumer<T> setter, Interpolator<T> interpolator) {
        this.getter = getter;
        this.setter = setter;
        this.interpolator = interpolator;
    }

    public static <T> TweenProperty<T> of(Supplier<T> getter, Consumer<T> setter, Interpolator<T> interpolator) {
        return new TweenProperty<T>(getter, setter, interpolator);
    }

    public TweenProperty<T> values(T startValue, T endValue) {
        this.startValue = Optional.ofNullable(startValue);
        this.endValue = Optional.ofNullable(endValue);
        return this;
    }

    public TweenProperty<T> duration(float duration) {
        this.duration = duration * 20.0f; // Transform in seconds
        return this;
    }

    public TweenProperty<T> easing(Easing easing) {
        this.easing = easing;
        return this;
    }

    public boolean isValid() {
        return startValue.isPresent() && endValue.isPresent() && duration > 0;
    }

    @Override
    public boolean update(float delta) {
        if (finished) return true;

        elapsed += delta;
        float progress = MathUtils.clamp(elapsed / duration, 0.0f, 1.0f);
        float easedProgress = easing.apply(progress);

        if (isValid())
            setter.accept(interpolator.lerp(startValue.get(), endValue.get(), easedProgress));

        if (progress >= 1.0f) {
            finished = true;
            callback();
        }
        return finished;
    }
}
