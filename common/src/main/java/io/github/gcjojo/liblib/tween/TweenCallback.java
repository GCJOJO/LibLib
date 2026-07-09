package io.github.gcjojo.liblib.tween;

public class TweenCallback extends Tween {
    private boolean hasRun = false;

    @Override
    public boolean update(float delta) {
        if (!hasRun) {
            callback();
            hasRun = true;
        }
        return true;
    }
}
