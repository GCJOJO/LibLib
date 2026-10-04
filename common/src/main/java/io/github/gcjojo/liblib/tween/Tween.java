package io.github.gcjojo.liblib.tween;

public abstract class Tween {

    protected Runnable onFinishCallback;

    protected void callback() {
        if (this.onFinishCallback != null)
            onFinishCallback.run();
    }

    @SuppressWarnings("unchecked") // TODO find better solution
    public <T extends Tween> T onFinished(Runnable onFinishCallback) {
        this.onFinishCallback = onFinishCallback;
        return (T) this;
    }

    public abstract boolean update(float delta);
}
