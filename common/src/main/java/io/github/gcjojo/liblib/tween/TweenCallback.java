package io.github.gcjojo.liblib.tween;

public class TweenCallback extends Tween {
    private boolean hasRun = false;

    public TweenCallback(Runnable callback) {
        this.onFinishCallback = callback;
    }

    public static TweenCallback of(Runnable callback) {
        TweenCallback tween = new TweenCallback(callback);
        return tween;
    }

    @Override
    public boolean update(float delta) {
        if (!hasRun) {
            callback();
            hasRun = true;
        }
        return true;
    }
}
