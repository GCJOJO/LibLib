package io.github.gcjojo.liblib.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public abstract class QuestsLibAPI {
    public static Event<QuestCompleted> QUEST_COMPLETED = EventFactory.createLoop();
    public static Event<TaskCompleted> TASK_COMPLETED = EventFactory.createLoop();

    public abstract boolean startQuest(Player player, ResourceLocation questId);

    public abstract boolean setQuestCompletionState(Player player, ResourceLocation questId, String newState);

    public abstract boolean setTaskCompletionState(Player player, ResourceLocation questId, ResourceLocation taskId, String newState);

    public abstract boolean isQuestCompleted(Player player, ResourceLocation questId);

    public abstract float getQuestCompletion(Player player, ResourceLocation questId);

    public abstract Optional<ResourceLocation> getCurrentTask(Player player, ResourceLocation questId);

    public interface QuestCompleted {
        void onQuestCompleted(Player player, ResourceLocation questId);
    }

    public interface TaskProgression {
        void onTaskProgression(Player player, ResourceLocation questId, ResourceLocation taskId, float progression);
    }

    public interface TaskCompleted {
        void onTaskCompleted(Player player, ResourceLocation questId, ResourceLocation taskId);
    }

    public static class EmptyQuestsLibAPI extends QuestsLibAPI {

        @Override
        public boolean startQuest(Player player, ResourceLocation questId) {
            return false;
        }

        @Override
        public boolean setQuestCompletionState(Player player, ResourceLocation questId, String newState) {
            return false;
        }

        @Override
        public boolean setTaskCompletionState(Player player, ResourceLocation questId, ResourceLocation taskId, String newState) {
            return false;
        }

        @Override
        public boolean isQuestCompleted(Player player, ResourceLocation questId) {
            return false;
        }

        @Override
        public float getQuestCompletion(Player player, ResourceLocation questId) {
            return 0;
        }

        @Override
        public Optional<ResourceLocation> getCurrentTask(Player player, ResourceLocation questId) {
            return Optional.empty();
        }
    }
}
