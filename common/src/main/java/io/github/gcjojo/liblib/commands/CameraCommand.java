package io.github.gcjojo.liblib.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.liblib.network.LibLibNetwork;
import io.github.gcjojo.liblib.tween.Easing;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.minecraft.modded.data.Coordinates;
import org.incendo.cloud.minecraft.modded.data.MultiplePlayerSelector;
import org.joml.Vector3f;

import java.util.Collection;

public class CameraCommand {

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

    @Command("camera <target> move <easeTime> <easeType> <easeFunction> <oldPos> <oldYaw> <oldPitch> <oldRoll> <newPos> <newYaw> <newPitch> <newRoll>")
    @Permission("select.op_level.2")
    public void cameraMovement(CommandSourceStack source,
                                @Argument("target") MultiplePlayerSelector targetPlayers,
                                @Argument("easeTime") float easeTime, @Argument("easeType") EaseType easeType, @Argument("easeFunction") EaseFunction easeFunction,
                                @Argument("oldPos") Coordinates oldPos,
                                @Argument("oldYaw") float oldYaw, @Argument("oldPitch") float oldPitch, @Argument("oldRoll") float oldRoll,
                                @Argument("newPos") Coordinates newPos,
                                @Argument("newYaw") float newYaw, @Argument("newPitch") float newPitch, @Argument("newRoll") float newRoll) throws CommandSyntaxException {

        if(!source.hasPermission(2))
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = easeTypeFunctionToEasing(easeType, easeFunction);
        NetworkManager.sendToPlayers(players, new LibLibNetwork.SendCameraMovementPayload(
                oldPos.position(), new Vector3f(oldYaw, oldPitch, oldRoll),
                newPos.position(), new Vector3f(newYaw, newPitch, newRoll),
                easing, easeTime));
        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, true));
    }

    @Command("camera <target> set pos <pos> rot <yaw> <pitch> <roll>")
    @Permission("select.op_level.2")
    public void cameraSet(CommandSourceStack source,
                             @Argument("target") MultiplePlayerSelector targetPlayers,
                             @Argument("pos") Coordinates cameraPos,
                             @Argument("yaw") float yaw, @Argument("pitch") float pitch, @Argument("roll") float roll) throws CommandSyntaxException {

        if(!source.hasPermission(2))
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        NetworkManager.sendToPlayers(players, new LibLibNetwork.SendCameraTargetPayload(cameraPos.position(), new Vector3f(yaw, pitch, roll),  Easing.LINEAR, 0));
        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, true));
    }

    @Command("camera <target> set pos <pos>")
    @Permission("select.op_level.2")
    public void cameraSetPos(CommandSourceStack source,
                          @Argument("target") MultiplePlayerSelector targetPlayers,
                          @Argument("pos") Coordinates cameraPos) throws CommandSyntaxException {
        if(!source.hasPermission(2))
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        NetworkManager.sendToPlayers(players, new LibLibNetwork.SendCameraPosTargetPayload(cameraPos.position(), Easing.LINEAR, 0));
        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, true));
    }

    @Command("camera <target> set rot <yaw> <pitch> <roll>")
    @Permission("select.op_level.2")
    public void cameraSetRot(CommandSourceStack source,
                          @Argument("target") MultiplePlayerSelector targetPlayers,
                          @Argument("yaw") float yaw, @Argument("pitch") float pitch, @Argument("roll") float roll) throws CommandSyntaxException {
        if(!source.hasPermission(2))
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        NetworkManager.sendToPlayers(players, new LibLibNetwork.SendCameraRotTargetPayload(new Vector3f(yaw, pitch, roll), Easing.LINEAR, 0));
        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, true));
    }

    @Command("camera <target> move <easeTime> <easeType> <easeFunction> pos <pos> rot <yaw> <pitch> <roll>")
    @Permission("select.op_level.2")
    public void cameraTarget(CommandSourceStack source,
                       @Argument("target") MultiplePlayerSelector targetPlayers,
                       @Argument("easeTime") float easeTime, @Argument("easeType") EaseType easeType, @Argument("easeFunction") EaseFunction easeFunction,
                       @Argument("pos") Coordinates cameraPos,
                       @Argument("yaw") float yaw, @Argument("pitch") float pitch, @Argument("roll") float roll) throws CommandSyntaxException {

        if(!source.hasPermission(2))
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = easeTypeFunctionToEasing(easeType, easeFunction);
        NetworkManager.sendToPlayers(players, new LibLibNetwork.SendCameraTargetPayload(cameraPos.position(), new Vector3f(yaw, pitch, roll),  easing, easeTime));
        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, true));
    }

    @Command("camera <target> move <easeTime> <easeType> <easeFunction> pos <pos>")
    @Permission("select.op_level.2")
    public void cameraTargetPos(CommandSourceStack source,
                          @Argument("target") MultiplePlayerSelector targetPlayers,
                          @Argument("easeTime") float easeTime, @Argument("easeType") EaseType easeType, @Argument("easeFunction") EaseFunction easeFunction,
                          @Argument("pos") Coordinates cameraPos) throws CommandSyntaxException {
        if(!source.hasPermission(2))
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = easeTypeFunctionToEasing(easeType, easeFunction);
        NetworkManager.sendToPlayers(players, new LibLibNetwork.SendCameraPosTargetPayload(cameraPos.position(), easing, easeTime));
        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, true));
    }

    @Command("camera <target> move <easeTime> <easeType> <easeFunction> rot <yaw> <pitch> <roll>")
    @Permission("select.op_level.2")
    public void cameraTargetRot(CommandSourceStack source,
                          @Argument("target") MultiplePlayerSelector targetPlayers,
                          @Argument("easeTime") float easeTime, @Argument("easeType") EaseType easeType, @Argument("easeFunction") EaseFunction easeFunction,
                          @Argument("yaw") float yaw, @Argument("pitch") float pitch, @Argument("roll") float roll) throws CommandSyntaxException {
        if(!source.hasPermission(2))
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = easeTypeFunctionToEasing(easeType, easeFunction);
        NetworkManager.sendToPlayers(players, new LibLibNetwork.SendCameraRotTargetPayload(new Vector3f(yaw, pitch, roll), easing, easeTime));
        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, true));
    }

    @Command("camera <target> clear")
    @Permission("select.op_level.2")
    public void clearCamera(CommandSourceStack source, @Argument("target") MultiplePlayerSelector targetPlayers) throws CommandSyntaxException {
        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        NetworkManager.sendToPlayers(players, new LibLibNetwork.ClearCameraPayload());
        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, false));
        NetworkManager.sendToPlayers(players, new LibLibNetwork.ClearFovPayload(Easing.LINEAR, 0));
    }

    @Command("camera <target> fov set <fov> [easeType] [easeFunction] [easeTime]")
    @Permission("select.op_level.2")
    public void setFov(CommandSourceStack source, @Argument("target") MultiplePlayerSelector targetPlayers,
                       @Argument("fov") @Range(min = "0", max = "179") double fov,
                       @Argument("easeType") @Default("ease_in_out") EaseType easeType, @Argument("easeFunction") @Default("ease_linear") EaseFunction easeFunction, @Argument("easeTime") @Default("0") float easeTime)
            throws CommandSyntaxException {
        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = easeTypeFunctionToEasing(easeType, easeFunction);

        NetworkManager.sendToPlayers(players, new LibLibNetwork.SetFovPayload(fov, easing, easeTime));
    }

    @Command("camera <target> fov set from <oldFov> to <newFov> <easeType> <easeFunction> <easeTime>")
    @Permission("select.op_level.2")
    public void setFovFromTo(CommandSourceStack source, @Argument("target") MultiplePlayerSelector targetPlayers,
                             @Argument("oldFov") @Range(min = "0", max = "179") double oldFov, @Argument("newFov") @Range(min = "0", max = "179") double newFov,
                             @Argument("easeType") EaseType easeType, @Argument("easeFunction") EaseFunction easeFunction, @Argument("easeTime") @Default("0") float easeTime)
            throws CommandSyntaxException {
        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = easeTypeFunctionToEasing(easeType, easeFunction);

        NetworkManager.sendToPlayers(players, new LibLibNetwork.SetFovVariationPayload(oldFov, newFov, easing, easeTime));
    }

    @Command("camera <target> fov clear [easeType] [easeFunction] [easeTime]")
    @Permission("select.op_level.2")
    public void clearFov(CommandSourceStack source, @Argument("target") MultiplePlayerSelector targetPlayers,
                         @Argument("easeType") @Default("ease_in_out") EaseType easeType, @Argument("easeFunction") @Default("ease_linear") EaseFunction easeFunction, @Argument("easeTime") @Default("0") float easeTime)
            throws CommandSyntaxException {
        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = easeTypeFunctionToEasing(easeType, easeFunction);

        NetworkManager.sendToPlayers(players, new LibLibNetwork.ClearFovPayload(easing, easeTime));
    }
}
