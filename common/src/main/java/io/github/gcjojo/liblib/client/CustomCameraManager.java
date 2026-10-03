package io.github.gcjojo.liblib.client;

import io.github.gcjojo.liblib.commands.CameraCommand;
import io.github.gcjojo.liblib.tween.Easing;
import io.github.gcjojo.liblib.tween.Interpolator;
import io.github.gcjojo.liblib.tween.TweenManager;
import io.github.gcjojo.liblib.tween.TweenSequence;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class CustomCameraManager {
    @Getter
    @Setter
    private static boolean isActive = false;
    @Getter
    @Setter
    private static Vec3 position = new Vec3(0, 0, 0);
    @Getter
    @Setter
    private static Vector3f rotation = new Vector3f(0, 0, 0);

    @Getter
    private static Vec3 targetPosition = new Vec3(0, 0, 0);
    @Getter
    private static Vector3f targetRotation = new Vector3f(0, 0, 0);

    public static void setCamera(Vec3 newPos, Vector3f newRot)
    {
        isActive = true;
        position = newPos;
        rotation = newRot;
    }

    public static void setCameraTarget(Vec3 newPos, Vector3f newRot, Easing easing, float easeTime)
    {
        targetPosition = newPos;
        targetRotation = newRot;

        TweenSequence sequence = TweenManager.createTweenSequence(TweenManager.TweenSide.CLIENT);
        sequence.setParallel(true);

        sequence.tweenProperty(CustomCameraManager::getPosition, CustomCameraManager::setPosition, Interpolator.VEC3)
                .duration(easeTime)
                .easing(easing)
                .values(position, targetPosition);

        sequence.tweenProperty(CustomCameraManager::getRotation, CustomCameraManager::setRotation, Interpolator.VECTOR3F)
                .duration(easeTime)
                .easing(easing)
                .values(rotation, targetRotation);

        sequence.play();
    }

    public static void setCamera(Vec3 oldPos, Vector3f oldRot, Vec3 newPos, Vector3f newRot, Easing easing, float easeTime)
    {
        setCamera(oldPos, oldRot);
        setCameraTarget(newPos, newRot, easing, easeTime);
    }
}
