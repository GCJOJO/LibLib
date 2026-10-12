package io.github.gcjojo.liblib.entity;

import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.gcjojo.liblib.LibLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;

public class LibLibEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(LibLib.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<Mannequin>> MANNEQUIN = ENTITIES.register("mannequin", () ->
            EntityType.Builder.of(Mannequin::create, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .eyeHeight(Avatar.DEFAULT_EYE_HEIGHT)
                    .vehicleAttachment(Avatar.DEFAULT_VEHICLE_ATTACHMENT)
                    .clientTrackingRange(32)
                    .build("mannequin"));

    public static void init() {
        ENTITIES.register();
        EntityAttributeRegistry.register(MANNEQUIN, LivingEntity::createLivingAttributes);
    }
}
