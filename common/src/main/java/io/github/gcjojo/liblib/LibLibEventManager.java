package io.github.gcjojo.liblib;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.gcjojo.liblib.events.LibLibEvents;
import io.github.gcjojo.liblib.utils.PlayerUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LibLibEventManager {
    public static Map<UUID, ChunkPos> playerChunks = new HashMap<>();
    public static Map<UUID, ResourceLocation> playerBiomes = new HashMap<>();
    public static Map<UUID, List<ItemStack>> playerInventories = new HashMap<>();
    public static int currentTick = 0;

    public static void registerEvents() {
        PlayerEvent.PLAYER_CLONE.register((ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wonGame) -> {
            LibLib.getPlayerDataManager().copyPlayer(oldPlayer, newPlayer);
        });

        TickEvent.PLAYER_POST.register(player -> {
            if (player.level().isClientSide()) return;
            if (!(player instanceof ServerPlayer serverPlayer)) return;

            UUID playerUUID = serverPlayer.getUUID();
            ChunkPos newChunkPos = serverPlayer.chunkPosition();
            if (!playerChunks.containsKey(playerUUID)) {
                playerChunks.putIfAbsent(playerUUID, newChunkPos);
                LibLibEvents.PLAYER_CHANGED_CHUNK.invoker().onPlayerChangedChunk(serverPlayer, newChunkPos);
                return;
            }

            ChunkPos oldChunkPos = playerChunks.get(playerUUID);
            if (newChunkPos.equals(oldChunkPos)) return;

            playerChunks.put(playerUUID, newChunkPos);
            LibLibEvents.PLAYER_CHANGED_CHUNK.invoker().onPlayerChangedChunk(serverPlayer, newChunkPos);

            // TODO Implement this in a way that allows us to know which items where changed
            if(currentTick % 20 != 0) return;

            if(!playerInventories.containsKey(playerUUID))
            {


            }

            NonNullList<ItemStack> newInventory = player.getInventory().items;
            List<ItemStack> oldInventory = playerInventories.get(playerUUID);
            if(!PlayerUtils.compareInventories(oldInventory, newInventory))
            {

            }
        });

        LibLibEvents.PLAYER_CHANGED_CHUNK.register((serverPlayer, newChunk) -> {
            checkPlayerBiomeChange(serverPlayer);
            checkPlayerInStructure(serverPlayer);
        });
    }

    public static void checkPlayerBiomeChange(ServerPlayer serverPlayer) {
        PlayerUtils.getPlayerBiome(serverPlayer).ifPresent(newBiomeId -> {
            UUID playerUUID = serverPlayer.getUUID();
            if (!playerBiomes.containsKey(playerUUID)) {
                playerBiomes.putIfAbsent(playerUUID, newBiomeId);
                LibLibEvents.PLAYER_ENTERED_BIOME.invoker().onPlayerEnterBiome(serverPlayer, newBiomeId);
                return;
            }

            ResourceLocation oldBiomeId = playerBiomes.get(playerUUID);
            if (newBiomeId.equals(oldBiomeId)) return;

            playerBiomes.put(playerUUID, newBiomeId);
            LibLibEvents.PLAYER_ENTERED_BIOME.invoker().onPlayerEnterBiome(serverPlayer, newBiomeId);
        });
    }

    public static void checkPlayerInStructure(ServerPlayer serverPlayer) {
        ServerLevel level = (ServerLevel) serverPlayer.level();

        level.registryAccess().registryOrThrow(Registries.STRUCTURE).keySet().forEach(structureId -> {
            if (PlayerUtils.isInStructure(serverPlayer, structureId))
                LibLibEvents.PLAYER_ENTERED_STRUCTURE.invoker().onPlayerEnteredStructure(serverPlayer, structureId);
        });
    }
}
