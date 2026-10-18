package io.github.gcjojo.liblib.neoforge.mixin;

import io.github.gcjojo.liblib.mixin.LibLibMixinPluginBase;
import net.neoforged.fml.loading.LoadingModList;

public class LibLibMixinPlugin extends LibLibMixinPluginBase {
    @Override
    protected boolean isSodiumLoaded() {
        LoadingModList list = LoadingModList.get();
        return  list != null && (list.getModFileById("sodium") != null || list.getModFileById("embeddium") != null);
    }
}
