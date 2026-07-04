package io.github.gcjojo.liblib.fabric;

import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.fabric.utils.FabricPlayerDataManager;
import net.fabricmc.api.ModInitializer;

public final class LibLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        LibLib.init();
        LibLib.setPlayerDataManager(new FabricPlayerDataManager());
    }
}
