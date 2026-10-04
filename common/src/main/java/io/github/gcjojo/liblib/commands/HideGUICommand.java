package io.github.gcjojo.liblib.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.liblib.network.LibLibNetwork;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.minecraft.modded.data.MultiplePlayerSelector;

import java.util.Collection;

public class HideGUICommand {
    @Command("hide hud hide [targets]")
    @Permission("select.op_level.2")
    public void hideHud(CommandSourceStack sourceStack,
                        @Argument("targets") MultiplePlayerSelector targetPlayers) throws CommandSyntaxException {
        if ((targetPlayers.values().isEmpty() && !sourceStack.isPlayer()))
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Collection<ServerPlayer> players = targetPlayers.values();
        if (players.isEmpty())
            players.add(sourceStack.getPlayer());

        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hud, true));
    }

    @Command("hide hud show [targets]")
    @Permission("select.op_level.2")
    public void showHud(CommandSourceStack sourceStack,
                        @Argument("targets") MultiplePlayerSelector targetPlayers) throws CommandSyntaxException {
        if ((targetPlayers.values().isEmpty() && !sourceStack.isPlayer()))
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Collection<ServerPlayer> players = targetPlayers.values();
        if (players.isEmpty())
            players.add(sourceStack.getPlayer());

        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hud, false));
    }

    @Command("hide hand hide [targets]")
    @Permission("select.op_level.2")
    public void hideHand(CommandSourceStack sourceStack,
                         @Argument("targets") MultiplePlayerSelector targetPlayers) throws CommandSyntaxException {
        if ((targetPlayers.values().isEmpty() && !sourceStack.isPlayer()))
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Collection<ServerPlayer> players = targetPlayers.values();
        if (players.isEmpty())
            players.add(sourceStack.getPlayer());

        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, true));
    }

    @Command("hide hand show [targets]")
    @Permission("select.op_level.2")
    public void showHand(CommandSourceStack sourceStack,
                         @Argument("targets") MultiplePlayerSelector targetPlayers) throws CommandSyntaxException {
        if ((targetPlayers.values().isEmpty() && !sourceStack.isPlayer()))
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Collection<ServerPlayer> players = targetPlayers.values();
        if (players.isEmpty())
            players.add(sourceStack.getPlayer());

        NetworkManager.sendToPlayers(players, new LibLibNetwork.HideHudPayload(LibLibNetwork.HideHudPayload.HideHudElement.Hand, false));
    }
}
