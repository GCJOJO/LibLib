package io.github.gcjojo.liblib.tween;

public class TweenParallel extends Tween {

    final boolean isParallel;

    private TweenParallel(boolean parallel) {
        this.isParallel = parallel;
    }

    public static TweenParallel of(boolean parallel) {
        return new TweenParallel(parallel);
    }

    @Override
    public boolean update(float delta) {
        return false;
    }
}
