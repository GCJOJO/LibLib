package io.github.gcjojo.liblib.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public abstract class BlablaLibAPI {
    public static Event<DialogueCompleted> DIALOGUE_COMPLETED = EventFactory.createLoop();

    public abstract void openDialogue(ServerPlayer player, ResourceLocation dialogueId);

    public abstract void setCurrentDialogue(ServerPlayer player, ResourceLocation dialogueId);

    public abstract boolean isPlayerInDialogue(ServerPlayer player);

    public abstract Optional<ResourceLocation> getPlayerLastReadDialogue(ServerPlayer player);

    public abstract void setPlayerLastReadDialogue(ServerPlayer player, ResourceLocation lastReadDialogueId);

    public interface DialogueCompleted {
        public void onDialogueCompleted(ServerPlayer player, ResourceLocation dialogueId);
    }

    public static class EmptyBlablaLibAPI extends BlablaLibAPI {

        @Override
        public void openDialogue(ServerPlayer player, ResourceLocation dialogueId) {
        }

        @Override
        public void setCurrentDialogue(ServerPlayer player, ResourceLocation dialogueId) {
        }

        @Override
        public boolean isPlayerInDialogue(ServerPlayer player) {
            return false;
        }

        @Override
        public Optional<ResourceLocation> getPlayerLastReadDialogue(ServerPlayer player) {
            return Optional.empty();
        }

        @Override
        public void setPlayerLastReadDialogue(ServerPlayer player, ResourceLocation lastReadDialogueId) {

        }
    }
}
