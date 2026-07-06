package io.github.gcjojo.liblib.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

public abstract class BlablaLibAPI {
    public static Event<DialogueCompleted> DIALOGUE_COMPLETED = EventFactory.createLoop();

    public abstract void openDialogue(ResourceLocation dialogueId);

    public abstract void setCurrentDialogue(ResourceLocation dialogueId);

    public interface DialogueCompleted {
        public void onDialogueCompleted(ResourceLocation dialogueId);
    }

    public static class EmptyBlablaLibAPI extends BlablaLibAPI {

        @Override
        public void openDialogue(ResourceLocation dialogueId) {
        }

        @Override
        public void setCurrentDialogue(ResourceLocation dialogueId) {
        }
    }
}
