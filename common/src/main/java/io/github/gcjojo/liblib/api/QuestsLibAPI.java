package io.github.gcjojo.liblib.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

public abstract class QuestsLibAPI {
    public static Event<QuestCompleted> QUEST_COMPLETED = EventFactory.createLoop();
    public static Event<TaskCompleted> TASK_COMPLETED = EventFactory.createLoop();

    public abstract void startQuest(ResourceLocation questId);

    public interface QuestCompleted {
        public void onQuestCompleted(ResourceLocation questId);
    }

    public interface TaskCompleted {
        public void onTaskCompleted(ResourceLocation taskId);
    }

    public static class EmptyQuestsLibAPI extends QuestsLibAPI {

        @Override
        public void startQuest(ResourceLocation questId) {
        }
    }
}
