package io.github.gcjojo.liblib.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.liblib.math.Color;
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
import org.incendo.cloud.minecraft.modded.data.MultiplePlayerSelector;

import java.util.Collection;

public class FadeCommand {

    @Command("fade push <target> <layer> <red> <green> <blue> <alpha> <easeTime> [easeType] [easeFunction]")
    @Permission("select.op_level.2")
    public void fade(CommandSourceStack sourceStack, @Argument("target") MultiplePlayerSelector playerSelector,
                     @Argument("layer") int layer,
                     @Argument("red") @Range(min = "0", max = "255") int startRed, @Argument("green") @Range(min = "0", max = "255") int startGreen, @Argument("blue") @Range(min = "0", max = "255") int startBlue, @Argument("alpha") @Range(min = "0", max = "255") int startAlpha,
                     @Argument("easeTime") @Default("0") float easeTime, @Argument("easeType") @Default("ease_in_out") CommandHelpers.EaseType easeType, @Argument("easeFunction") @Default("ease_linear") CommandHelpers.EaseFunction easeFunction) throws CommandSyntaxException {

        Collection<ServerPlayer> players = playerSelector.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = CommandHelpers.easeTypeFunctionToEasing(easeType, easeFunction);

        Color endColor = new Color(startRed, startGreen, startBlue, startAlpha);

        NetworkManager.sendToPlayers(players, new LibLibNetwork.FadePayload(layer, Color.TRANSPARENT, endColor, easing, easeTime));
    }

    @Command("fade push <target> <layer> from <startRed> <startGreen> <startBlue> <startAlpha> to <endRed> <endGreen> <endBlue> <endAlpha> <easeTime> [easeType] [easeFunction]")
    @Permission("select.op_level.2")
    public void fadeFromTo(CommandSourceStack sourceStack, @Argument("target") MultiplePlayerSelector playerSelector,
                           @Argument("layer") int layer,
                           @Argument("startRed") @Range(min = "0", max = "255") int startRed, @Argument("startGreen") @Range(min = "0", max = "255") int startGreen, @Argument("startBlue") @Range(min = "0", max = "255") int startBlue, @Argument("startAlpha") @Range(min = "0", max = "255") int startAlpha,
                           @Argument("endRed") @Range(min = "0", max = "255") int endRed, @Argument("endGreen") @Range(min = "0", max = "255") int endGreen, @Argument("endBlue") @Range(min = "0", max = "255") int endBlue, @Argument("endAlpha") @Range(min = "0", max = "255") int endAlpha,
                           @Argument("easeTime") @Default("0") float easeTime, @Argument("easeType") @Default("ease_in_out") CommandHelpers.EaseType easeType, @Argument("easeFunction") @Default("ease_linear") CommandHelpers.EaseFunction easeFunction) throws CommandSyntaxException {

        Collection<ServerPlayer> players = playerSelector.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Easing easing = CommandHelpers.easeTypeFunctionToEasing(easeType, easeFunction);

        Color startColor = new Color(startRed, startGreen, startBlue, startAlpha);
        Color endColor = new Color(endRed, endGreen, endBlue, endAlpha);

        NetworkManager.sendToPlayers(players, new LibLibNetwork.FadePayload(layer, startColor, endColor, easing, easeTime));
    }

    @Command("fade clear <target> <layer>")
    @Permission("select.op_level.2")
    public void clearFade(CommandSourceStack sourceStack, @Argument("target") MultiplePlayerSelector playerSelector,
                     @Argument("layer") int layer) throws CommandSyntaxException {

        Collection<ServerPlayer> players = playerSelector.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        NetworkManager.sendToPlayers(players, new LibLibNetwork.ClearFadePayload(layer));
    }

    @Command("fade clear <target> all")
    @Permission("select.op_level.2")
    public void clearAllFade(CommandSourceStack sourceStack, @Argument("target") MultiplePlayerSelector playerSelector) throws CommandSyntaxException {
        Collection<ServerPlayer> players = playerSelector.values();

        if(players.isEmpty())
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        NetworkManager.sendToPlayers(players, new LibLibNetwork.ClearAllFadesPayload());
    }
}
