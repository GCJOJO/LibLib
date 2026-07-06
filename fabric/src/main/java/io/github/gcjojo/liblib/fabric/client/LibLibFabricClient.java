package io.github.gcjojo.liblib.fabric.client;

import io.github.gcjojo.liblib.LibLib;
import net.fabricmc.api.ClientModInitializer;

public final class LibLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LibLib.initClient();
        LibLib.setSoundPlayer(new FabricSoundPlayer());
    }
}
