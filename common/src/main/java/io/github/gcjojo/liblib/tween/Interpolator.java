package io.github.gcjojo.liblib.tween;

import io.github.gcjojo.liblib.math.Color;
import io.github.gcjojo.liblib.utils.MathUtils;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

@FunctionalInterface
public interface Interpolator<T> {
    Interpolator<Boolean> BOOL = (start, end, progress) -> progress >= 1.0f ? end : start;
    Interpolator<Float> FLOAT = MathUtils::lerp;
    Interpolator<Integer> INTEGER = MathUtils::lerp;
    Interpolator<Double> DOUBLE = MathUtils::lerp;
    Interpolator<Vec2> VEC2 = MathUtils::lerp;
    Interpolator<Vec3> VEC3 = MathUtils::lerp;
    Interpolator<Vector3f> VECTOR3F = MathUtils::lerp;
    Interpolator<Vector3f> ROTATION = MathUtils::quaternion_lerp;
    Interpolator<Color> COLOR = MathUtils::lerp;


    T lerp(T start, T end, float progress);
}

