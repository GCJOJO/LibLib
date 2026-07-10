package io.github.gcjojo.liblib.tween;

public class TweenWait extends Tween {
    private float waitTime = 0.0f;
    private float elapsedTime = 0.0f;

    public TweenWait(float waitTime) {
        this.waitTime = waitTime * 20.0f;
    }

    @Override
    public boolean update(float delta) {
        elapsedTime += delta;
        if (elapsedTime >= waitTime) {
            callback();
            return true;
        }
        return false;
    }
}
