package io.github.gcjojo.liblib.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Collection;

public abstract class CutsceneLibAPI {
    public abstract void playCutscene(ServerPlayer player, ResourceLocation cutsceneId);
    public abstract void playCutscene(ServerPlayer player, ResourceLocation cutsceneId, Vec3 anchorPosition, Vector3f anchorRotation);
    public abstract void playCutscene(Collection<ServerPlayer> players, ResourceLocation cutsceneId);
    public abstract void playCutscene(Collection<ServerPlayer> players, ResourceLocation cutsceneId, Vec3 anchorPosition, Vector3f anchorRotation);

    public static final class EmptyCutsceneLibAPI extends CutsceneLibAPI {
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
