package io.github.gcjojo.liblib.tween;

import io.github.gcjojo.liblib.utils.MathUtils;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface Interpolator<T> {
    Interpolator<Float> FLOAT = MathUtils::lerp;
    Interpolator<Integer> INTEGER = MathUtils::lerp;
    Interpolator<Double> DOUBLE = MathUtils::lerp;
    Interpolator<Vec2> VEC2 = MathUtils::lerp;
    Interpolator<Vec3> VEC3 = MathUtils::lerp;

    T lerp(T start, T end, float progress);
}

