package io.github.gcjojo.liblib.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public abstract class CutsceneLibAPI {
    public abstract void playCutscene(ServerPlayer player, ResourceLocation cutsceneId);
    public abstract void playCutscene(Collection<ServerPlayer> players, ResourceLocation cutsceneId);

    public static final class EmptyCutsceneLibAPI extends CutsceneLibAPI {
        @Override
        public void playCutscene(ServerPlayer player, ResourceLocation cutsceneId) { }

        @Override
        public void playCutscene(Collection<ServerPlayer> players, ResourceLocation cutsceneId) { }
    }
}
