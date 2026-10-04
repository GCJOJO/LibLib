package io.github.gcjojo.liblib.client;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.ImprovedNoise;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class CameraEffects {

    private static final ImprovedNoise NOISE = new ImprovedNoise(RandomSource.create());

    // Claude Slop pour les maths
    private static float noise(float t, float seed) {
        /*return (Mth.sin(t * 1.0f + seed)
                + Mth.sin(t * 2.3f + seed * 1.7f) * 0.5f
                + Mth.sin(t * 4.1f + seed * 2.9f) * 0.25f) / 1.75f; // normalisé dans [-1, 1]*/
        return (float) NOISE.noise(t, seed, 0.5);
    }

    public static CameraEffect shakePosition(float intensity, float speed)
    {
        return (time) -> {
            float t = time * speed;
            Vec3 offset = new Vec3(
                    noise(t, 10.31f),
                    noise(t, 47.73f),
                    noise(t, 91.17f)
            ).scale(intensity);
            return new CameraEffect.CameraTransform(offset, new Vector3f(0, 0, 0));
        };
    }

    public static CameraEffect shakeRotation(float intensity, float speed) {
        return time -> {
            float t = time * speed;
            Vector3f rot = new Vector3f(
                    noise(t, 133.41f),
                    noise(t, 171.89f),
                    noise(t, 209.23f)
            ).mul(intensity);
            return new CameraEffect.CameraTransform(Vec3.ZERO, rot);
        };
    }

    // Merci ClaudSlop pour les maths

    /// Exemple usages:
    /// <pre>
    /// {@code
    /// // Rises in 0.3 s, holds for 1 s, decays in 0.7 s
    /// new Envelope(CameraEffects.shakePosition(0.15f, 15f), 0.3f, 1f, 0.7f);
    ///
    /// // No rise, no hold, just decay
    /// new Envelope(CameraEffects.shakeRotation(2f, 20f), 0f, 0f, 0.5f);
    ///
    /// // Continuous Shake with a slow rise, only stops if clear() is called
    /// new Envelope(shake, 1f, Float.POSITIVE_INFINITY, 0f);
    /// }
    /// </pre>
    /// @param inner Effect inside Enveloppe
    /// @param attack Time to reach full effect
    /// @param hold Time for full effect to hold
    /// @param decay Time for full effect to decay
    public record Enveloppe(CameraEffect inner, float attack, float hold, float decay) implements CameraEffect
    {
        float total() { return attack + hold + decay; }

        @Override
        public boolean isFinished(float time) { return time >= total(); }

        private float factor(float time) {
            if (time < attack) {
                // Montée : progression 0 -> 1, adoucie (smoothstep)
                float t = attack <= 0f ? 1f : time / attack;
                return t * t * (3f - 2f * t);
            }
            if (time < attack + hold) {
                return 1f;
            }
            // Descente : 1 -> 0, quadratique
            float t = decay <= 0f ? 1f : (time - attack - hold) / decay;
            float k = Mth.clamp(1f - t, 0f, 1f);
            return k * k;
        }

        @Override
        public CameraTransform apply(float time) {
            float k = factor(time);
            CameraTransform tr = inner.apply(time);
            return new CameraTransform(
                    tr.positionOffset().scale(k),
                    new Vector3f(tr.rotationOffset()).mul(k)
            );
        }
    }
}
