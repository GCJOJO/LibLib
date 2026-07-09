package io.github.gcjojo.liblib.forge;

import dev.architectury.platform.forge.EventBuses;
import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.forge.client.ForgeSoundPlayer;
import io.github.gcjojo.liblib.forge.utils.ForgePlayerDataManager;
import io.github.gcjojo.liblib.tween.TweenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LibLib.MOD_ID)
public final class LibLibForge {
    public LibLibForge(FMLJavaModLoadingContext context) {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(LibLib.MOD_ID, context.getModEventBus());

        // Run our common setup.
        LibLib.init();
        LibLib.setPlayerDataManager(new ForgePlayerDataManager());
    }

    @Mod.EventBusSubscriber(modid = LibLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEntryForge {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LibLib.initClient();
            LibLib.setSoundPlayer(new ForgeSoundPlayer());
        }
    }

    @Mod.EventBusSubscriber(modid = LibLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientModForge {
        @SubscribeEvent
        public static void onRenderFrame(ViewportEvent event) {
            TweenManager.updateSequences((float) event.getPartialTick(), TweenManager.TweenSide.CLIENT);
        }
    }
}
