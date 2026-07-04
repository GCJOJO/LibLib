package io.github.gcjojo.liblib.fabric.client;

import net.fabricmc.api.ClientModInitializer;

public final class LibLibFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LibLib.setSoundPlayer(new FabricSoundPlayer());
    }
}
