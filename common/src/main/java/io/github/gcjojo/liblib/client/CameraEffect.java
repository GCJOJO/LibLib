package io.github.gcjojo.liblib.client;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public interface CameraEffect {
    CameraTransform apply(float time);

    default boolean isFinished(float time) { return false; }

    record CameraTransform(Vec3 positionOffset, Vector3f rotationOffset){
        public static final CameraTransform DEFAULT = new CameraTransform(Vec3.ZERO, new Vector3f(0, 0, 0));
    }
}
