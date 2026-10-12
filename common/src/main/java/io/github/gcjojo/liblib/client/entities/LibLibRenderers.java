package io.github.gcjojo.liblib.client.entities;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import io.github.gcjojo.liblib.entity.LibLibEntities;

public class LibLibRenderers {
    public static void init() {
        EntityRendererRegistry.register(LibLibEntities.MANNEQUIN, ctx -> new MannequinRenderer(ctx, false));
    }
}
