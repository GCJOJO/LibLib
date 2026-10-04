package io.github.gcjojo.liblib.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.liblib.network.LibLibNetwork;
import io.github.gcjojo.liblib.tween.Easing;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
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

    @Command("camera <target> move <easeTime> <easeType> <easeFunction> <oldPos> <oldYaw> <oldPitch> <oldRoll> <newPos> <newYaw> <newPitch> <newRoll>")
    @Permission("select.op_level.2")
    public void cameraMovement(CommandSourceStack source,
                                @Argument("target") MultiplePlayerSelector targetPlayers,
                                @Argument("easeTime") float easeTime, @Argument("easeType") CommandHelpers.EaseType easeType, @Argument("easeFunction") CommandHelpers.EaseFunction easeFunction,
                                @Argument("oldPos") Coordinates oldPos,
                                @Argument("oldYaw") float oldYaw, @Argument("oldPitch") float oldPitch, @Argument("oldRoll") float oldRoll,
                                @Argument("newPos") Coordinates newPos,
                                @Argument("newYaw") float newYaw, @Argument("newPitch") float newPitch, @Argument("newRoll") float newRoll) throws CommandSyntaxException {

        if(!source.hasPermission(2))
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = CommandHelpers.easeTypeFunctionToEasing(easeType, easeFunction);
        NetworkManager.sendToPlayers(players, new LibLibNetwork.CameraMovementPayload(
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

        NetworkManager.sendToPlayers(players, new LibLibNetwork.CameraTargetPayload(cameraPos.position(), new Vector3f(yaw, pitch, roll),  Easing.LINEAR, 0));
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

        NetworkManager.sendToPlayers(players, new LibLibNetwork.CameraPosTargetPayload(cameraPos.position(), Easing.LINEAR, 0));
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

        NetworkManager.sendToPlayers(players, new LibLibNetwork.CameraRotTargetPayload(new Vector3f(yaw, pitch, roll), Easing.LINEAR, 0));
        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, true));
    }

    @Command("camera <target> move <easeTime> <easeType> <easeFunction> pos <pos> rot <yaw> <pitch> <roll>")
    @Permission("select.op_level.2")
    public void cameraTarget(CommandSourceStack source,
                       @Argument("target") MultiplePlayerSelector targetPlayers,
                       @Argument("easeTime") float easeTime, @Argument("easeType") CommandHelpers.EaseType easeType, @Argument("easeFunction") CommandHelpers.EaseFunction easeFunction,
                       @Argument("pos") Coordinates cameraPos,
                       @Argument("yaw") float yaw, @Argument("pitch") float pitch, @Argument("roll") float roll) throws CommandSyntaxException {

        if(!source.hasPermission(2))
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = CommandHelpers.easeTypeFunctionToEasing(easeType, easeFunction);
        NetworkManager.sendToPlayers(players, new LibLibNetwork.CameraTargetPayload(cameraPos.position(), new Vector3f(yaw, pitch, roll),  easing, easeTime));
        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, true));
    }

    @Command("camera <target> move <easeTime> <easeType> <easeFunction> pos <pos>")
    @Permission("select.op_level.2")
    public void cameraTargetPos(CommandSourceStack source,
                          @Argument("target") MultiplePlayerSelector targetPlayers,
                          @Argument("easeTime") float easeTime, @Argument("easeType") CommandHelpers.EaseType easeType, @Argument("easeFunction") CommandHelpers.EaseFunction easeFunction,
                          @Argument("pos") Coordinates cameraPos) throws CommandSyntaxException {
        if(!source.hasPermission(2))
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = CommandHelpers.easeTypeFunctionToEasing(easeType, easeFunction);
        NetworkManager.sendToPlayers(players, new LibLibNetwork.CameraPosTargetPayload(cameraPos.position(), easing, easeTime));
        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, true));
    }

    @Command("camera <target> move <easeTime> <easeType> <easeFunction> rot <yaw> <pitch> <roll>")
    @Permission("select.op_level.2")
    public void cameraTargetRot(CommandSourceStack source,
                          @Argument("target") MultiplePlayerSelector targetPlayers,
                          @Argument("easeTime") float easeTime, @Argument("easeType") CommandHelpers.EaseType easeType, @Argument("easeFunction") CommandHelpers.EaseFunction easeFunction,
                          @Argument("yaw") float yaw, @Argument("pitch") float pitch, @Argument("roll") float roll) throws CommandSyntaxException {
        if(!source.hasPermission(2))
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();

        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = CommandHelpers.easeTypeFunctionToEasing(easeType, easeFunction);
        NetworkManager.sendToPlayers(players, new LibLibNetwork.CameraRotTargetPayload(new Vector3f(yaw, pitch, roll), easing, easeTime));
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
                       @Argument("easeType") @Default("ease_in_out") CommandHelpers.EaseType easeType, @Argument("easeFunction") @Default("ease_linear") CommandHelpers.EaseFunction easeFunction, @Argument("easeTime") @Default("0") float easeTime)
            throws CommandSyntaxException {
        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = CommandHelpers.easeTypeFunctionToEasing(easeType, easeFunction);

        NetworkManager.sendToPlayers(players, new LibLibNetwork.SetFovPayload(fov, easing, easeTime));
    }

    @Command("camera <target> fov set from <oldFov> to <newFov> <easeType> <easeFunction> <easeTime>")
    @Permission("select.op_level.2")
    public void setFovFromTo(CommandSourceStack source, @Argument("target") MultiplePlayerSelector targetPlayers,
                             @Argument("oldFov") @Range(min = "0", max = "179") double oldFov, @Argument("newFov") @Range(min = "0", max = "179") double newFov,
                             @Argument("easeType") CommandHelpers.EaseType easeType, @Argument("easeFunction") CommandHelpers.EaseFunction easeFunction, @Argument("easeTime") @Default("0") float easeTime)
            throws CommandSyntaxException {
        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = CommandHelpers.easeTypeFunctionToEasing(easeType, easeFunction);

        NetworkManager.sendToPlayers(players, new LibLibNetwork.SetFovVariationPayload(oldFov, newFov, easing, easeTime));
    }

    @Command("camera <target> fov clear [easeType] [easeFunction] [easeTime]")
    @Permission("select.op_level.2")
    public void clearFov(CommandSourceStack source, @Argument("target") MultiplePlayerSelector targetPlayers,
                         @Argument("easeType") @Default("ease_in_out") CommandHelpers.EaseType easeType, @Argument("easeFunction") @Default("ease_linear") CommandHelpers.EaseFunction easeFunction, @Argument("easeTime") @Default("0") float easeTime)
            throws CommandSyntaxException {
        Collection<ServerPlayer> players = targetPlayers.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = CommandHelpers.easeTypeFunctionToEasing(easeType, easeFunction);

        NetworkManager.sendToPlayers(players, new LibLibNetwork.ClearFovPayload(easing, easeTime));
    }

    public static void doShake(CommandSourceStack source, Collection<ServerPlayer> players, CommandHelpers.ShakeType shakeType, int layer, float intensity, float speed, float duration, float attack, float decay) {
        NetworkManager.sendToPlayers(players, new LibLibNetwork.ShakePayload(shakeType, layer, intensity, speed, new LibLibNetwork.Enveloppe(duration, attack, decay)));
    }

    @Command("camera <target> shake set <layer> <type> <intensity> <speed> <duration> [attack] [decay]")
    @Permission("select.op_level.2")
    public void shake(CommandSourceStack source,
                                @Argument("target") MultiplePlayerSelector playerSelector, @Argument("type") CommandHelpers.ShakeType shakeType, @Argument("layer") int layer,
                                @Argument("intensity") float intensity, @Argument("speed") float speed,
                                @Argument("duration") float duration, @Argument("attack") @Default("0") float attack, @Argument("decay") @Default("0") float decay) throws CommandSyntaxException {
        Collection<ServerPlayer> players = playerSelector.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        doShake(source, players, shakeType, layer, intensity, speed, duration, attack, decay);
    }

    @Command("camera <target> shake set <layer> <type> <intensity> <speed> infinite [attack] [decay]")
    @Permission("select.op_level.2")
    public void shakeInfinite(CommandSourceStack source,
                                @Argument("target") MultiplePlayerSelector playerSelector, @Argument("type") CommandHelpers.ShakeType shakeType, @Argument("layer") int layer,
                                @Argument("intensity") float intensity, @Argument("speed") float speed,
                                @Argument("attack") @Default("0") float attack, @Argument("decay") @Default("0") float decay) throws CommandSyntaxException {
        Collection<ServerPlayer> players = playerSelector.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        doShake(source, players, shakeType, layer, intensity, speed, Float.POSITIVE_INFINITY, attack, decay);
    }

    @Command("camera <target> shake clear <layer>")
    @Permission("select.op_level.2")
    public void clearShake(CommandSourceStack source, @Argument("target") MultiplePlayerSelector playerSelector, @Argument("type") CommandHelpers.ShakeType shakeType, @Argument("layer") int layer) throws CommandSyntaxException {
        Collection<ServerPlayer> players = playerSelector.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();
    }

    @Command("camera <target> shake clear all")
    @Permission("select.op_level.2")
    public void clearAllShake(CommandSourceStack source, @Argument("target") MultiplePlayerSelector playerSelector, @Argument("type") CommandHelpers.ShakeType shakeType) throws CommandSyntaxException {
        Collection<ServerPlayer> players = playerSelector.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();
    }
}
