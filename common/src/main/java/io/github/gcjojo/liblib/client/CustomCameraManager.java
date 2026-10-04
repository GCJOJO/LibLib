package io.github.gcjojo.liblib.client;

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
    private static boolean isFovActive = false;
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

    @Getter
    @Setter
    private static double internalFov = 0;

    @Getter
    @Setter
    private static double fov = 0;
    @Getter
    @Setter
    private static double targetFov = 0;

    static TweenSequence positionSequence = null;
    static TweenSequence rotationSequence = null;
    static TweenSequence fovSequence = null;

    public static void setCameraMovement(Vec3 newPos, Vector3f newRot)
    {
        isActive = true;
        position = newPos;
        rotation = newRot;
    }

    public static void setPositionTarget(Vec3 newPos, Easing easing, float easeTime)
    {
        if(positionSequence != null && positionSequence.isPlaying())
            positionSequence.stop();

        targetPosition = newPos;
        positionSequence = TweenManager.createTweenSequence(TweenManager.TweenSide.CLIENT);

        positionSequence.tweenProperty(CustomCameraManager::getPosition, CustomCameraManager::setPosition, Interpolator.VEC3)
                .duration(easeTime)
                .easing(easing)
                .values(position, targetPosition);

        positionSequence.play();
    }

    public static void setRotationTarget(Vector3f newRot, Easing easing, float easeTime)
    {
        if(rotationSequence != null && rotationSequence.isPlaying())
            rotationSequence.stop();

        targetRotation = newRot;
        rotationSequence = TweenManager.createTweenSequence(TweenManager.TweenSide.CLIENT);

        rotationSequence.tweenProperty(CustomCameraManager::getRotation, CustomCameraManager::setRotation, Interpolator.VECTOR3F)
                .duration(easeTime)
                .easing(easing)
                .values(rotation, targetRotation);

        rotationSequence.play();
    }

    public static void setCameraTarget(Vec3 newPos, Vector3f newRot, Easing easing, float easeTime)
    {
        setPositionTarget(newPos, easing, easeTime);
        setRotationTarget(newRot, easing, easeTime);
    }

    public static void setCameraMovement(Vec3 oldPos, Vector3f oldRot, Vec3 newPos, Vector3f newRot, Easing easing, float easeTime)
    {
        setCameraMovement(oldPos, oldRot);
        setCameraTarget(newPos, newRot, easing, easeTime);
    }

    public static void setTargetFov(double newTargetFov, Easing easing, float easeTime)
    {
        isFovActive = true;

        if(fovSequence == null)
            fovSequence.stop();

        targetFov = newTargetFov;
        fovSequence = TweenManager.createTweenSequence(TweenManager.TweenSide.CLIENT);

        fovSequence.tweenProperty(CustomCameraManager::getFov, CustomCameraManager::setFov, Interpolator.DOUBLE)
                .duration(easeTime)
                .easing(easing)
                .values(fov, targetFov);

        fovSequence.play();
    }

    public static void setFovVariation(double oldFov, double newTargetFov, Easing easing, float easeTime)
    {
        isFovActive = true;

        if(fovSequence == null)
            fovSequence.stop();

        fov = oldFov;
        targetFov = newTargetFov;
        fovSequence = TweenManager.createTweenSequence(TweenManager.TweenSide.CLIENT);

        fovSequence.tweenProperty(CustomCameraManager::getFov, CustomCameraManager::setFov, Interpolator.DOUBLE)
                .duration(easeTime)
                .easing(easing)
                .values(fov, targetFov);

        fovSequence.play();
    }

    public static void clearFov(Easing easing, float easeTime)
    {
        if(easeTime == 0)
        {
            isFovActive = false;
            return;
        }

        setFovVariation(fov, internalFov, easing, easeTime);
        fovSequence.tweenCallback(() -> isFovActive = false);
    }
}
