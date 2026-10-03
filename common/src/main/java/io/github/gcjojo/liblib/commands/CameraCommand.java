package io.github.gcjojo.liblib.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.liblib.network.LibLibNetwork;
import io.github.gcjojo.liblib.tween.Easing;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.minecraft.modded.data.Coordinates;
import org.incendo.cloud.minecraft.modded.data.MultiplePlayerSelector;
import org.joml.Vector3f;

import java.util.Collection;

public class CameraCommand {

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

    @Command("camera <target> move <easeTime> <easeType> <easeFunction> pos <pos> rot <yaw> <pitch> <roll>")
    public void camera(CommandSourceStack source,
                       @Argument("target") MultiplePlayerSelector targetPlayers,
                       @Argument("easeTime") float easeTime, @Argument("easeType") EaseType easeType, @Argument("easeFunction") EaseFunction easeFunction,
                       @Argument("pos") Coordinates cameraPos,
                       @Argument("yaw") float yaw, @Argument("pitch") float pitch, @Argument("roll") float roll) throws CommandSyntaxException {

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

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

        NetworkManager.sendToPlayers(players, new LibLibNetwork.SendCameraPayload(cameraPos.position(), new Vector3f(yaw, pitch, roll),  easing, easeTime));
    }

    @Command("camera <target> clear")
    public void clearCamera(CommandSourceStack source, @Argument("target") MultiplePlayerSelector targetPlayers) throws CommandSyntaxException {
        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        NetworkManager.sendToPlayers(players, new LibLibNetwork.ClearCameraPayload());
    }
}
