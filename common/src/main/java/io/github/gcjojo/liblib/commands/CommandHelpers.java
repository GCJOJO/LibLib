package io.github.gcjojo.liblib.commands;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.gcjojo.liblib.tween.Easing;
import net.minecraft.network.chat.Component;

public class CommandHelpers {
    private static final SimpleCommandExceptionType ERROR_MISMATCHED_EASE =
            new SimpleCommandExceptionType(Component.literal("easeType and easeFunction must be set together or both absent")); // TODO Translatable

    public enum EaseType
    {
        EASE_IN,
        EASE_OUT,
        EASE_IN_OUT
    }

    public enum EaseFunction
    {
        EASE_LINEAR,
        EASE_SINE,
        EASE_CUBIC,
        EASE_QUAD,
        EASE_BACK,
        EASE_BOUNCE
    }

    public enum ShakeType {
        POSITIONAL,
        ROTATIONAL
    }

    public static Easing easeTypeFunctionToEasing(EaseType easeType, EaseFunction easeFunction)
    {
        Easing easing = Easing.LINEAR;
        switch(easeFunction) {
            case EASE_SINE -> {
                switch (easeType) {
                    case EASE_IN -> easing = Easing.Sine.EASE_IN;
                    case EASE_OUT -> easing = Easing.Sine.EASE_OUT;
                    case EASE_IN_OUT -> easing = Easing.Sine.EASE_IN_OUT;
                }
            }
            case EASE_CUBIC -> {
                switch (easeType) {
                    case EASE_IN -> easing = Easing.Cubic.EASE_IN;
                    case EASE_OUT -> easing = Easing.Cubic.EASE_OUT;
                    case EASE_IN_OUT -> easing = Easing.Cubic.EASE_IN_OUT;
                }
            }
            case EASE_QUAD -> {
                switch (easeType) {
                    case EASE_IN -> easing = Easing.Quad.EASE_IN;
                    case EASE_OUT -> easing = Easing.Quad.EASE_OUT;
                    case EASE_IN_OUT -> easing = Easing.Quad.EASE_IN_OUT;
                }
            }
            case EASE_BACK -> {
                switch (easeType) {
                    case EASE_IN -> easing = Easing.Back.EASE_IN;
                    case EASE_OUT -> easing = Easing.Back.EASE_OUT;
                    case EASE_IN_OUT -> easing = Easing.Back.EASE_IN_OUT;
                }
            }
            case EASE_BOUNCE -> {
                switch (easeType) {
                    case EASE_IN -> easing = Easing.Bounce.EASE_IN;
                    case EASE_OUT -> easing = Easing.Bounce.EASE_OUT;
                    case EASE_IN_OUT -> easing = Easing.Bounce.EASE_IN_OUT;
                }
            }
        }
        return easing;
    }
}
