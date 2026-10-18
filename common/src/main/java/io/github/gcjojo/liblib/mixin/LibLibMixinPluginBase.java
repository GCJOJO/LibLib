package io.github.gcjojo.liblib.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

// Claude Slop
public abstract class LibLibMixinPluginBase implements IMixinConfigPlugin {
    private static final org.apache.logging.log4j.Logger LOG =
            org.apache.logging.log4j.LogManager.getLogger("LibLibMixinPlugin");

    private boolean sodiumLoaded;

    protected abstract boolean isSodiumLoaded();

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean apply = !(mixinClassName.endsWith("LevelRendererMixin") && this.sodiumLoaded);
        LOG.info("shouldApplyMixin {} -> {}", mixinClassName, apply);
        return apply;
    }

    @Override public void onLoad(String mixinPackage) {
        this.sodiumLoaded = isSodiumLoaded();
        LOG.info("Mixin plugin loaded, sodium/embeddium detected = {}", this.sodiumLoaded);
    }
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String t, ClassNode c, String m, IMixinInfo i) {}
    @Override public void postApply(String t, ClassNode c, String m, IMixinInfo i) {}
}
