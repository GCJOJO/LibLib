package io.github.gcjojo.liblib.client;

import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.liblib.network.LibLibNetwork;
import io.github.gcjojo.liblib.tween.Easing;
import io.github.gcjojo.liblib.tween.Interpolator;
import io.github.gcjojo.liblib.tween.TweenManager;
import io.github.gcjojo.liblib.tween.TweenSequence;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Iterator;
import java.util.TreeMap;

public class ClientCameraManager {
    @Getter
    @Setter
    private static boolean isActive = false;
    @Getter
    @Setter
    private static boolean isFovActive = false;
    @Getter
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

    private static int tickCount = 0;

    static TweenSequence positionSequence = null;
    static TweenSequence rotationSequence = null;
    static TweenSequence fovSequence = null;

    public record ActiveEffect(CameraEffect effect, long startTime){}

    static final TreeMap<Integer, ActiveEffect> EFFECTS = new TreeMap<>();

    public static void setPosition(Vec3 newPosition) {
        position = newPosition;
        // TODO FIX BEFORE USING THIS FEATURE
        /*if(isActive())
            NetworkManager.sendToServer(new LibLibNetwork.ClientCameraPosPayload(position));*/
    }

    public static void tick() {
        if(!isActive())
            return;

        tickCount++;
        if(tickCount >= 10)
        {
            tickCount = 0;
            NetworkManager.sendToServer(new LibLibNetwork.ClientCameraPosPayload(position));
        }
    }

    public static void clearCustomCamera()
    {
        ClientCameraManager.setActive(false);
        ClientCameraManager.setFovActive(false);
        NetworkManager.sendToServer(new LibLibNetwork.ClientClearCustomCameraPayload());
        Minecraft.getInstance().levelRenderer.needsUpdate();
    }

    public static boolean hasAnyEffect() { return !EFFECTS.isEmpty(); }

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

        positionSequence.tweenProperty(ClientCameraManager::getPosition, ClientCameraManager::setPosition, Interpolator.VEC3)
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

        rotationSequence.tweenProperty(ClientCameraManager::getRotation, ClientCameraManager::setRotation, Interpolator.VECTOR3F)
                .duration(easeTime)
                .easing(easing)
                .values(rotation, targetRotation);

        rotationSequence.play();
    }

    public static void setCameraTarget(Vec3 newPos, Vector3f newRot, Easing easing, float easeTime)
    {
        if(easeTime == 0)
        {
            setPosition(newPos);
            setRotation(newRot);
            return;
        }

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
        if(easeTime == 0)
            fov = newTargetFov;
        else
            setFovFromTo(fov, newTargetFov, easing, easeTime);
    }

    public static void setFovFromTo(double oldFov, double newTargetFov, Easing easing, float easeTime)
    {
        isFovActive = true;

        if(fovSequence != null)
            fovSequence.stop();

        fov = oldFov;
        targetFov = newTargetFov;
        fovSequence = TweenManager.createTweenSequence(TweenManager.TweenSide.CLIENT);

        fovSequence.tweenProperty(ClientCameraManager::getFov, ClientCameraManager::setFov, Interpolator.DOUBLE)
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

        setFovFromTo(fov, internalFov, easing, easeTime);
        fovSequence.tweenCallback(() -> isFovActive = false);
    }

    public static void setCameraEffect(int layer, CameraEffect effect)
    {
        ActiveEffect activeEffect = new ActiveEffect(effect, Util.getMillis());
        EFFECTS.put(layer, activeEffect);
    }

    public static void clearCameraEffect(int layer)
    {
        EFFECTS.remove(layer);
        Minecraft.getInstance().levelRenderer.needsUpdate();
    }

    public static void clearCameraEffects()
    {
        EFFECTS.clear();
        Minecraft.getInstance().levelRenderer.needsUpdate();
    }

    public static CameraEffect.CameraTransform computeOffsets()
    {
        if(EFFECTS.isEmpty())
            return CameraEffect.CameraTransform.DEFAULT;

        Vec3 pos = Vec3.ZERO;
        Vector3f rot = new Vector3f();

        Iterator<ActiveEffect> it = EFFECTS.values().iterator();
        while (it.hasNext()) {
            ActiveEffect activeEffect = it.next();
            float time = (Util.getMillis() - activeEffect.startTime) / 1000f;

            if(activeEffect.effect.isFinished(time))
            {
                it.remove();
                continue;
            }

            CameraEffect.CameraTransform t = activeEffect.effect().apply(time);
            pos = pos.add(t.positionOffset());
            rot.add(t.rotationOffset());
        }

        return new CameraEffect.CameraTransform(pos, rot);
    }
}
