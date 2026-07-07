package io.github.gcjojo.liblib.utils;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class MathUtils {
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
}
