package io.github.gcjojo.liblib.fabric.mixin;

import io.github.gcjojo.liblib.mixin.LibLibMixinPluginBase;
import net.fabricmc.loader.api.FabricLoader;

public class LibLibMixinPlugin extends LibLibMixinPluginBase {
    @Override
    protected boolean isSodiumLoaded() {
        return FabricLoader.getInstance().isModLoaded("sodium");
    }
}
