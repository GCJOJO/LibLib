package io.github.gcjojo.liblib.neoforge;

import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.neoforge.client.NeoForgeSoundPlayer;
import io.github.gcjojo.liblib.neoforge.utils.NeoForgePlayerDataManager;
import io.github.gcjojo.liblib.tween.TweenManager;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.neoforge.NeoForgeServerCommandManager;

@Mod(LibLib.MOD_ID)
public final class LibLibNeoForge {

    public LibLibNeoForge() {
        // Run our common setup.
        LibLib.init();
        LibLib.setPlayerDataManager(new NeoForgePlayerDataManager());

        NeoForgeServerCommandManager<CommandSourceStack> commandManager =
                new NeoForgeServerCommandManager<>(ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity());

        LibLib.registerCommands(commandManager);
    }

    @Mod(value = LibLib.MOD_ID, dist = Dist.CLIENT)
    public static class ClientModEntryForge {
        public ClientModEntryForge() {
            LibLib.initClient();
            LibLib.setSoundPlayer(new NeoForgeSoundPlayer());

            NeoForge.EVENT_BUS.register(new ClientEventHandler());
        }
    }

    public static class ClientEventHandler {
        @SubscribeEvent
        public void onRenderFrame(RenderFrameEvent.Pre event) {
            TweenManager.updateSequences(event.getPartialTick().getGameTimeDeltaTicks(), TweenManager.TweenSide.CLIENT);
            
        }
    }
}
