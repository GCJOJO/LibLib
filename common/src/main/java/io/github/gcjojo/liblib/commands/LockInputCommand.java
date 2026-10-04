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

public class LockInputCommand {
    @Command("input keyboard lock [targets]")
    @Permission("select.op_level.2")
    public void lockKeyboard(CommandSourceStack sourceStack,
                        @Argument("targets") MultiplePlayerSelector targetPlayers) throws CommandSyntaxException {
        if ((targetPlayers.values().isEmpty() && !sourceStack.isPlayer()))
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Collection<ServerPlayer> players = targetPlayers.values();
        if (players.isEmpty())
            players.add(sourceStack.getPlayer());

        NetworkManager.sendToPlayers(players, new LibLibNetwork.LockInputPayload(LibLibNetwork.LockInputPayload.InputType.Keyboard, true));
    }

    @Command("input keyboard unlock [targets]")
    @Permission("select.op_level.2")
    public void unlockKeyboard(CommandSourceStack sourceStack,
                        @Argument("targets") MultiplePlayerSelector targetPlayers) throws CommandSyntaxException {
        if ((targetPlayers.values().isEmpty() && !sourceStack.isPlayer()))
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Collection<ServerPlayer> players = targetPlayers.values();
        if (players.isEmpty())
            players.add(sourceStack.getPlayer());

        NetworkManager.sendToPlayers(players, new LibLibNetwork.LockInputPayload(LibLibNetwork.LockInputPayload.InputType.Keyboard, false));
    }

    @Command("input mouse lock [targets]")
    @Permission("select.op_level.2")
    public void lockMouse(CommandSourceStack sourceStack,
                         @Argument("targets") MultiplePlayerSelector targetPlayers) throws CommandSyntaxException {
        if ((targetPlayers.values().isEmpty() && !sourceStack.isPlayer()))
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Collection<ServerPlayer> players = targetPlayers.values();
        if (players.isEmpty())
            players.add(sourceStack.getPlayer());

        NetworkManager.sendToPlayers(players, new LibLibNetwork.LockInputPayload(LibLibNetwork.LockInputPayload.InputType.Mouse, true));
    }

    @Command("input mouse unlock [targets]")
    @Permission("select.op_level.2")
    public void unlockMouse(CommandSourceStack sourceStack,
                         @Argument("targets") MultiplePlayerSelector targetPlayers) throws CommandSyntaxException {
        if ((targetPlayers.values().isEmpty() && !sourceStack.isPlayer()))
            throw EntityArgument.NO_PLAYERS_FOUND.create();

        Collection<ServerPlayer> players = targetPlayers.values();
        if (players.isEmpty())
            players.add(sourceStack.getPlayer());

        NetworkManager.sendToPlayers(players, new LibLibNetwork.LockInputPayload(LibLibNetwork.LockInputPayload.InputType.Mouse, false));
    }
}
