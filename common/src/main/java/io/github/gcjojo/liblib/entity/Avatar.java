package io.github.gcjojo.liblib.entity;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// Backported from 26.1
public abstract class Avatar extends LivingEntity {
    public static final HumanoidArm DEFAULT_MAIN_HAND = HumanoidArm.RIGHT;
    public static final int DEFAULT_MODEL_CUSTOMIZATION = 0;
    public static final float DEFAULT_EYE_HEIGHT = 1.62F;
    public static final Vec3 DEFAULT_VEHICLE_ATTACHMENT = new Vec3(0.0D, 0.6D, 0.0D);;
    private static final float CROUCH_BB_HEIGHT = 1.5F;
    private static final float SWIMMING_BB_WIDTH = 0.6F;
    public static final float SWIMMING_BB_HEIGHT = 0.6F;

    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MAIN_HAND =
            SynchedEntityData.defineId(Avatar.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION =
            SynchedEntityData.defineId(Avatar.class, EntityDataSerializers.BYTE);

    protected static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.8F)
            .withEyeHeight(DEFAULT_EYE_HEIGHT)
            .withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, DEFAULT_VEHICLE_ATTACHMENT));


    protected static final Map<Pose, EntityDimensions> POSES = ImmutableMap.<Pose, EntityDimensions>builder()
            .put(Pose.STANDING, STANDING_DIMENSIONS)
            .put(Pose.SLEEPING, SLEEPING_DIMENSIONS)
            .put(Pose.FALL_FLYING, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F))
            .put(Pose.SWIMMING, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F))
            .put(Pose.SPIN_ATTACK, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F))
            .put(Pose.CROUCHING, EntityDimensions.scalable(0.6F, 1.5F).withEyeHeight(1.27F)
                    .withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, DEFAULT_VEHICLE_ATTACHMENT)))
            .put(Pose.DYING, EntityDimensions.fixed(0.2F, 0.2F).withEyeHeight(DEFAULT_EYE_HEIGHT))
            .build();



    protected Avatar(final EntityType<? extends LivingEntity> type, final Level level) {
        super(type, level);
    }

    protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_PLAYER_MAIN_HAND, armToByte(DEFAULT_MAIN_HAND));
        entityData.define(DATA_PLAYER_MODE_CUSTOMISATION, (byte)0);
    }

    private static byte armToByte(final HumanoidArm arm) {
        return (byte) (arm == HumanoidArm.LEFT ? 0 : 1);
    }


    public @NotNull HumanoidArm getMainArm() {
        return this.entityData.get(DATA_PLAYER_MAIN_HAND) == 0 ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }

    public void setMainArm(final HumanoidArm mainArm) {
        this.entityData.set(DATA_PLAYER_MAIN_HAND, armToByte(mainArm));
    }

    public boolean isModelPartShown(final PlayerModelPart part) {
        return ((Byte)this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & part.getMask()) == part.getMask();
    }

    public @NotNull EntityDimensions getDefaultDimensions(final Pose pose) {
        return (EntityDimensions)POSES.getOrDefault(pose, STANDING_DIMENSIONS);
    }

    public abstract ResolvableProfile getProfile();
}
