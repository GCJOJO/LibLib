package io.github.gcjojo.liblib;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import io.github.gcjojo.liblib.events.LibLibEvents;
import io.github.gcjojo.liblib.utils.PlayerUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LibLibEventManager {
    public static Map<UUID, ChunkPos> playerChunks = new HashMap<>();
    public static Map<UUID, ResourceLocation> playerBiomes = new HashMap<>();
    public static Map<UUID, Map<ResourceLocation, Integer>> playerInventories = new HashMap<>();
    public static int currentTick = 0;

    public static ResourceLocation PLAYER_INVENTORY_SAVE_DATA = ResourceLocation.tryBuild(LibLib.MOD_ID, "player_inventory");

    public static void registerEvents() {
        PlayerEvent.PLAYER_JOIN.register((ServerPlayer player) -> {
            PlayerInventorySaveData inventorySaveData = LibLib.getPlayerDataManager().deserializePlayerData(player, PLAYER_INVENTORY_SAVE_DATA, PlayerInventorySaveData.class);
            playerInventories.putIfAbsent(player.getUUID(), inventorySaveData.getPlayerInventory());
        });

        LifecycleEvent.SERVER_LEVEL_UNLOAD.register((serverLevel) -> {
            playerChunks.clear();
            playerBiomes.clear();
            playerInventories.clear();
            currentTick = 0;
        });

        PlayerEvent.PLAYER_CLONE.register((ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wonGame) -> {
            LibLib.getPlayerDataManager().copyPlayer(oldPlayer, newPlayer);
        });

        TickEvent.PLAYER_POST.register(player -> {
            tickPlayerChunks(player);
            tickPlayerInventories(player);
        });

        LibLibEvents.PLAYER_CHANGED_CHUNK.register((serverPlayer, newChunk) -> {
            checkPlayerBiomeChange(serverPlayer);
            checkPlayerInStructure(serverPlayer);
        });
    }

    public static void tickPlayerChunks(Player player) {
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
    }

    public static void tickPlayerInventories(Player player) {
        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        UUID playerUUID = serverPlayer.getUUID();

        // Delay for each player
        if ((currentTick + playerUUID.hashCode()) % 20 != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        Map<ResourceLocation, Integer> newInventory = PlayerUtils.getInventoryItemAmounts(level, player.getInventory().items);
        if (newInventory.isEmpty()) return;

        Map<ResourceLocation, Integer> inventoryDifference = newInventory;

        if (playerInventories.containsKey(playerUUID)) {
            Map<ResourceLocation, Integer> oldInventory = playerInventories.get(playerUUID);
            inventoryDifference = PlayerUtils.compareInventories(level, oldInventory, newInventory);
            if (inventoryDifference.isEmpty()) return;

            playerInventories.put(playerUUID, newInventory);
        }

        playerInventories.put(playerUUID, newInventory);
        LibLibEvents.PLAYER_INVENTORY_CHANGED.invoker().onPlayerInventoryChanged(serverPlayer, inventoryDifference);

        PlayerInventorySaveData inventorySaveData = LibLib.getPlayerDataManager().deserializePlayerData(player, PLAYER_INVENTORY_SAVE_DATA, PlayerInventorySaveData.class);
        inventorySaveData.playerInventory = newInventory;
        LibLib.getPlayerDataManager().serializePlayerData(player, inventorySaveData, PLAYER_INVENTORY_SAVE_DATA);
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
