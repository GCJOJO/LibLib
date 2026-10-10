package io.github.gcjojo.liblib.math;

import io.github.gcjojo.liblib.utils.MathUtils;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

// Claude Slop
public final class CameraMath {
    /** rot = (pitch, yaw, roll) en degrés → quaternion d'orientation de la caméra. */
    public static Quaternionf toQuat(Vector3f rot) {
        return new Quaternionf().rotationYXZ(
                (float) Math.PI - MathUtils.toRad(rot.y),
                -MathUtils.toRad(rot.x),
                MathUtils.toRad(rot.z));
    }

    /** Inverse exact de toQuat. */
    public static Vector3f toRot(Quaternionf q) {
        Vector3f e = q.getEulerAnglesYXZ(new Vector3f()); // x, y, z en radians
        float yaw = 180f - (float) Math.toDegrees(e.y);
        return new Vector3f(
                -(float) Math.toDegrees(e.x),             // pitch
                Mth.wrapDegrees(yaw),                     // yaw
                (float) Math.toDegrees(e.z));             // roll
    }
}
