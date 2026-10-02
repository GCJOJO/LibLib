package io.github.gcjojo.liblib.utils;

import io.github.gcjojo.liblib.math.Color;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class MathUtils {
    public static double TO_DEG_DOUBLE = 180.0d / Math.PI;
    public static double TO_RAD_DOUBLE = Math.PI / 180.0d;

    public static float TO_DEG_FLOAT = 180.0f / (float) Math.PI;
    public static float TO_RAD_FLOAT = (float) Math.PI / 180.0f;

    public static double toDeg(double rad) {
        return rad * TO_DEG_DOUBLE;
    }

    public static double toRad(double deg) {
        return deg * TO_RAD_DOUBLE;
    }

    public static float toDeg(float rad) {
        return rad * TO_DEG_FLOAT;
    }

    public static float toRad(float deg) {
        return deg * TO_RAD_FLOAT;
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public static double lerp(double start, double end, float progress) {
        return start + (end - start) * progress;
    }

    public static float lerp(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    public static int lerp(int start, int end, float progress) {
        return Math.round((float) start + ((float) end - (float) start) * progress);
    }

    public static Vec2 lerp(Vec2 start, Vec2 end, float progress) {
        return new Vec2(lerp(start.x, end.x, progress), lerp(start.y, end.y, progress));
    }

    public static Vec3 lerp(Vec3 start, Vec3 end, float progress) {
        return new Vec3(lerp(start.x, end.x, progress), lerp(start.y, end.y, progress), lerp(start.z, end.z, progress));
    }

    public static Vector3f lerp(Vector3f start, Vector3f end, float progress) {
        return new Vector3f(lerp(start.x, end.x, progress), lerp(start.y, end.y, progress), lerp(start.z, end.z, progress));
    }

    public static Color lerp(Color start, Color end, float progress) {
        return new Color(
                lerp(start.alpha, end.alpha, progress),
                lerp(start.red, end.red, progress),
                lerp(start.green, end.green, progress),
                lerp(start.blue, end.blue, progress)
        );
    }
}
