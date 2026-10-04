package io.github.gcjojo.liblib;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;

import java.util.*;

// Claude Slop
public class CameraChunkLoader {
    private static final TicketType<ChunkPos> TICKET =
            TicketType.create("liblib_camera", Comparator.comparingLong(ChunkPos::toLong));

    private static final int BUDGET_PER_TICK = 24;   // chunks envoyés par tick
    private static final int TIMEOUT_TICKS = 100;    // plus de paquet client pendant 5 s : on arrête

    private static final class State {
        ChunkPos center;
        int radius;
        long lastPacketTick;
        final Set<Long> sent = new HashSet<>();
        final Set<Long> pending = new LinkedHashSet<>();
    }

    private static final Map<UUID, State> STATES = new HashMap<>();

    private static boolean inRange(long packed, ChunkPos center, int r) {
        ChunkPos c = new ChunkPos(packed);
        return Math.abs(c.x - center.x) <= r && Math.abs(c.z - center.z) <= r;
    }

    // Appelé par le receveur du paquet (thread serveur, via context.queue)
    public static void onClientPos(ServerPlayer player, Vec3 pos) {
        ServerLevel level = player.serverLevel();
        State s = STATES.computeIfAbsent(player.getUUID(), id -> new State());
        s.lastPacketTick = level.getServer().getTickCount();

        ChunkPos center = new ChunkPos(BlockPos.containing(pos));
        if (center.equals(s.center)) return;

        s.center = center;
        s.radius = Math.min(level.getServer().getPlayerList().getViewDistance(),
                player.requestedViewDistance());

        // Le client abandonne ce qui sort de sa nouvelle fenêtre
        player.connection.send(new ClientboundSetChunkCacheCenterPacket(center.x, center.z));
        s.sent.removeIf(l -> !inRange(l, center, s.radius));
        s.pending.removeIf(l -> !inRange(l, center, s.radius));

        // Tout ce qui est dans la fenêtre et pas encore envoyé passe en attente
        for (int dx = -s.radius; dx <= s.radius; dx++) {
            for (int dz = -s.radius; dz <= s.radius; dz++) {
                long l = ChunkPos.asLong(center.x + dx, center.z + dz);
                if (!s.sent.contains(l)) s.pending.add(l);
            }
        }
    }

    // À brancher sur TickEvent.SERVER_POST
    public static void tick(MinecraftServer server) {
        if (STATES.isEmpty()) return;

        Iterator<Map.Entry<UUID, State>> it = STATES.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, State> entry = it.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            State s = entry.getValue();

            // Joueur parti, ou plus de nouvelles du client : on arrête proprement
            if (player == null) { it.remove(); continue; }
            if (s.center == null) continue;
            if (server.getTickCount() - s.lastPacketTick > TIMEOUT_TICKS) {
                it.remove();
                restorePlayerChunkPosition(player);
                continue;
            }

            ServerLevel level = player.serverLevel();

            // Un seul ticket au centre, renouvelé à chaque tick (évite l'expiration)
            level.getChunkSource().addRegionTicket(TICKET, s.center, s.radius + 1, s.center);

            // Les plus proches du centre d'abord
            ChunkPos center = s.center;
            List<Long> ordered = new ArrayList<>(s.pending);
            ordered.sort(Comparator.comparingInt(l -> {
                ChunkPos c = new ChunkPos(l);
                return Math.max(Math.abs(c.x - center.x), Math.abs(c.z - center.z));
            }));

            int budget = BUDGET_PER_TICK;
            for (long l : ordered) {
                if (budget <= 0) break;
                ChunkPos cp = new ChunkPos(l);

                // Non bloquant : null tant que le chunk n'est pas prêt, il reste alors en attente
                LevelChunk chunk = level.getChunkSource().getChunkNow(cp.x, cp.z);
                if (chunk == null) continue;

                player.connection.send(
                        new ClientboundLevelChunkWithLightPacket(chunk, level.getLightEngine(), null, null));
                s.pending.remove(l);
                s.sent.add(l);
                budget--;
            }
        }
    }

    // Fin de cinématique (paquet « fin » du client, timeout, déconnexion)
    public static void resetPlayerChunkPosition(ServerPlayer player) {
        if (STATES.remove(player.getUUID()) != null) restorePlayerChunkPosition(player);

    }

    private static void restorePlayerChunkPosition(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        ChunkPos home = player.chunkPosition();
        int r = Math.min(level.getServer().getPlayerList().getViewDistance(),
                player.requestedViewDistance());

        player.connection.send(new ClientboundSetChunkCacheCenterPacket(home.x, home.z));

        // Le serveur croit que le client a déjà ces chunks : on les renvoie à la main
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(home.x + dx, home.z + dz);
                if (chunk != null) {
                    player.connection.send(
                            new ClientboundLevelChunkWithLightPacket(chunk, level.getLightEngine(), null, null));
                }
            }
        }
    }
}
