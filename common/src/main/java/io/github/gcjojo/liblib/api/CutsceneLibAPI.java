package io.github.gcjojo.liblib.api;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Collection;

@SuppressWarnings("unused")
public abstract class CutsceneLibAPI {
    public static Event<CutsceneCompleted> CUTSCENE_COMPLETED = EventFactory.createLoop();

    public abstract void playCutsceneLocal(ResourceLocation cutsceneId);
    public abstract void playCutsceneLocal(ResourceLocation cutsceneId, Vec3 anchorPosition, Vector3f anchorRotation);

    public abstract void playCutscene(ServerPlayer player, ResourceLocation cutsceneId);
    public abstract void playCutscene(ServerPlayer player, ResourceLocation cutsceneId, Vec3 anchorPosition, Vector3f anchorRotation);
    public abstract void playCutscene(Collection<ServerPlayer> players, ResourceLocation cutsceneId);
    public abstract void playCutscene(Collection<ServerPlayer> players, ResourceLocation cutsceneId, Vec3 anchorPosition, Vector3f anchorRotation);

    public interface CutsceneCompleted {
        void onCutsceneCompleted(ResourceLocation cutsceneId);
    }

    public static final class EmptyCutsceneLibAPI extends CutsceneLibAPI {
        @Override
        public void playCutsceneLocal(ResourceLocation cutsceneId) { }

        @Override
        public void playCutsceneLocal(ResourceLocation cutsceneId, Vec3 anchorPosition, Vector3f anchorRotation) { }

        @Override
        public void playCutscene(ServerPlayer player, ResourceLocation cutsceneId) { }

        @Override
        public void playCutscene(ServerPlayer player, ResourceLocation cutsceneId, Vec3 anchorPosition, Vector3f anchorRotation) { }

        @Override
        public void playCutscene(Collection<ServerPlayer> players, ResourceLocation cutsceneId) { }

        @Override
        public void playCutscene(Collection<ServerPlayer> players, ResourceLocation cutsceneId, Vec3 anchorPosition, Vector3f anchorRotation) { }
    }
}
