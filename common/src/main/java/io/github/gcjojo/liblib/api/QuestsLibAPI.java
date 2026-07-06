package io.github.gcjojo.liblib.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public abstract class QuestsLibAPI {
    public static Event<QuestCompleted> QUEST_COMPLETED = EventFactory.createLoop();
    public static Event<TaskCompleted> TASK_COMPLETED = EventFactory.createLoop();

    public abstract void startQuest(Player player, ResourceLocation questId);

    public interface QuestCompleted {
        public void onQuestCompleted(Player player, ResourceLocation questId);
    }

    public interface TaskCompleted {
        public void onTaskCompleted(Player player, ResourceLocation taskId);
    }

    public static class EmptyQuestsLibAPI extends QuestsLibAPI {

        @Override
        public void startQuest(Player player, ResourceLocation questId) {
        }
    }
}
