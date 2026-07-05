package io.github.gcjojo.liblib.events;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public interface LibLibEvents {
    Event<PlayerChangedChunk> PLAYER_CHANGED_CHUNK = EventFactory.createLoop();
    Event<PlayerEnteredBiome> PLAYER_ENTERED_BIOME = EventFactory.createLoop();
    Event<PlayerEnteredStructure> PLAYER_ENTERED_STRUCTURE = EventFactory.createLoop();

    public interface PlayerChangedChunk {
        void onPlayerChangedChunk(ServerPlayer serverPlayer, ChunkPos newChunk);
    }

    public interface PlayerEnteredBiome {
        void onPlayerEnterBiome(ServerPlayer serverPlayer, ResourceLocation newBiomeId);
    }

    public interface PlayerEnteredStructure {
        void onPlayerEnteredStructure(ServerPlayer serverPlayer, ResourceLocation newStructureId);
    }

    public interface PlayerInventoryChanged {

    }
}
