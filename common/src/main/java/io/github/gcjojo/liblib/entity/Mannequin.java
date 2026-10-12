package io.github.gcjojo.liblib.entity;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.github.gcjojo.liblib.LibLib;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

// Backported from 26.1 with help from claude slop

/**
 * <pre>
 *
 * Summon Mannequin with a player skin
 * /summon liblib:mannequin ~ ~ ~ {profile:{name:"Dinnerbone"}}
 *
 * // Steve and Alex
 * /summon liblib:mannequin ~ ~ ~ {profile:{id:[I;0,0,0,1]}}
 * /summon liblib:mannequin ~ ~ ~ {profile:{id:[I;0,0,0,2]}}
 *
 *
 * /summon liblib:mannequin ~ ~ ~ {pose:"crouching"}
 * /summon liblib:mannequin ~ ~ ~ {hidden_layers:["hat","jacket"],main_hand:"left"}
 * /summon liblib:mannequin ~ ~ ~ {immovable:1b,description:"Test"}
 *
 * Poses : standing, crouching, swimming, fall_flying, sleeping.
 *
 * Equipment :
 * /item replace entity @e[type=liblib:mannequin,limit=1,sort=nearest] armor.head with minecraft:diamond_helmet
 * /item replace entity @e[type=liblib:mannequin,limit=1,sort=nearest] weapon.mainhand with minecraft:diamond_sword
 *
 * Edit a mannequin :
 *
 * /data merge entity @e[type=liblib:mannequin,limit=1,sort=nearest] {pose:"swimming"}
 * /data get entity @e[type=liblib:mannequin,limit=1,sort=nearest]
 *
 * /summon liblib:mannequin ~ ~ ~ {walk_speed:1.0f}
 * /data merge entity @e[type=liblib:mannequin,limit=1,sort=nearest] {walk_speed:0.5f}
 * /data merge entity @e[type=liblib:mannequin,limit=1,sort=nearest] {walk_speed:0.0f}
 * </pre>
 */
public class Mannequin extends Avatar {

    protected static final EntityDataAccessor<CompoundTag> DATA_PROFILE =
            SynchedEntityData.defineId(Mannequin.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Boolean> DATA_IMMOVABLE =
            SynchedEntityData.defineId(Mannequin.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<Component>> DATA_DESCRIPTION =
            SynchedEntityData.defineId(Mannequin.class, EntityDataSerializers.OPTIONAL_COMPONENT);
    private static final EntityDataAccessor<Float> DATA_WALK_SPEED =
            SynchedEntityData.defineId(Mannequin.class, EntityDataSerializers.FLOAT);


    private static final byte ALL_LAYERS = computeAllLayers();
    private static final Set<Pose> VALID_POSES =
            Set.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING, Pose.FALL_FLYING, Pose.SLEEPING);

    public static final Codec<Pose> POSE_CODEC =
            Codec.STRING.comapFlatMap(Mannequin::poseByName,
        pose -> pose.name().toLowerCase(Locale.ROOT));

    private static final Codec<PlayerModelPart> PART_CODEC = Codec.STRING.comapFlatMap(Mannequin::partById,
            PlayerModelPart::getId);

    private static final Codec<Byte> LAYERS_CODEC = PART_CODEC.listOf().xmap(
            hiddenParts -> {
                int mask = ALL_LAYERS;
                for (PlayerModelPart part : hiddenParts) {
                    mask &= ~part.getMask();
                }
                return (byte) mask;
            },
            mask -> {
                java.util.List<PlayerModelPart> hidden = new java.util.ArrayList<>();
                for (PlayerModelPart part : PlayerModelPart.values()) {
                    if ((mask & part.getMask()) == 0) {
                        hidden.add(part);
                    }
                }
                return hidden;
            });


    public static final ResolvableProfile DEFAULT_PROFILE
            = new ResolvableProfile(Optional.empty(), Optional.empty(), new PropertyMap());


    private static final Component DEFAULT_DESCRIPTION = Component.translatable("entity.liblib.mannequin.label");

    protected static EntityType.EntityFactory<Mannequin> constructor = Mannequin::new;

    // NBT FIELDS
    private static final String PROFILE_FIELD = "profile";
    private static final String HIDDEN_LAYERS_FIELD = "hidden_layers";
    private static final String MAIN_HAND_FIELD = "main_hand";
    private static final String POSE_FIELD = "pose";
    private static final String IMMOVABLE_FIELD = "immovable";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String HIDE_DESCRIPTION_FIELD = "hide_description";
    private static final String WALK_SPEED_FIELD = "walk_speed";

    // State
    private Component description;
    private boolean hideDescription;
    private @Nullable ResolvableProfile profileCache;

    private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);

    public Mannequin(final EntityType<Mannequin> type, final Level level) {
        super(type, level);
        this.description = DEFAULT_DESCRIPTION;
        this.hideDescription = false;
        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, ALL_LAYERS);
    }

    protected Mannequin(final Level level) {
        this(LibLibEntities.MANNEQUIN.get(), level);
    }

    public static @Nullable Mannequin create(final EntityType<Mannequin> type, final Level level) {
        return constructor.create(type, level);
    }

    protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_PROFILE, encodeProfile(DEFAULT_PROFILE));
        entityData.define(DATA_IMMOVABLE, false);
        entityData.define(DATA_DESCRIPTION, Optional.of(DEFAULT_DESCRIPTION));
        entityData.define(DATA_WALK_SPEED, 0.0f);
    }

    @Override
    public void onSyncedDataUpdated(final EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_PROFILE.equals(key)) {
            this.profileCache = null;
        }
    }

    public ResolvableProfile getProfile() {
        if (this.profileCache == null) {
            this.profileCache = ResolvableProfile.CODEC
                    .parse(NbtOps.INSTANCE, this.entityData.get(DATA_PROFILE))
                    .resultOrPartial(err -> LibLib.getLogger().error("Failed to decode mannequin profile: {}", err))
                    .orElse(DEFAULT_PROFILE);
        }
        return this.profileCache;
    }

    private void setProfile(final ResolvableProfile profile) {
        this.entityData.set(DATA_PROFILE, encodeProfile(profile));

        if (!this.level().isClientSide && this.level().getServer() != null && !profile.isResolved()) {
            profile.resolve().thenAcceptAsync(this::setProfile, this.level().getServer());
        }
    }

    private static CompoundTag encodeProfile(final ResolvableProfile profile) {
        return ResolvableProfile.CODEC.encodeStart(NbtOps.INSTANCE, profile)
                .resultOrPartial(err -> LibLib.getLogger().error("Failed to encode mannequin profile: {}", err))
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .orElseGet(CompoundTag::new);
    }

    private boolean getImmovable() {
        return this.entityData.get(DATA_IMMOVABLE);
    }

    private void setImmovable(final boolean immovable) {
        this.entityData.set(DATA_IMMOVABLE, immovable);
    }

    protected @Nullable Component getDescription() {
        return (this.entityData.get(DATA_DESCRIPTION)).orElse(null);
    }

    private void setDescription(final Component description) {
        this.description = description;
        this.updateDescription();
    }

    private void setHideDescription(final boolean hideDescription) {
        this.hideDescription = hideDescription;
        this.updateDescription();
    }

    private void updateDescription() {
        this.entityData.set(DATA_DESCRIPTION, this.hideDescription ? Optional.empty() : Optional.of(this.description));
    }

    protected boolean isImmobile() {
        return this.getImmovable() || super.isImmobile();
    }

    public boolean isEffectiveAi() {
        return !this.getImmovable() && super.isEffectiveAi();
    }

    public float getWalkSpeed() {
        return this.entityData.get(DATA_WALK_SPEED);
    }

    public void setWalkSpeed(final float walkSpeed) {
        this.entityData.set(DATA_WALK_SPEED, Mth.clamp(walkSpeed, 0.0f, 1.0f));
    }

    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();

        final float walkSpeed = getWalkSpeed();
        if(walkSpeed > 0.0f) {
            this.walkAnimation.update(walkSpeed, 0.4f);
        }
        else if(this.walkAnimation.speed() > 0.001f) {
            this.walkAnimation.update(0.0f, 0.4f);
        }
    }

    public void updateWalkAnimation(final float distance) {
        if(getWalkSpeed() <= 0.0f) {
            super.updateWalkAnimation(distance);
        }
    }

    @Override
    public @NotNull Iterable<ItemStack> getHandSlots() {
        return this.handItems;
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return this.armorItems;
    }

    @Override
    public @NotNull ItemStack getItemBySlot(final EquipmentSlot slot) {
        return switch (slot.getType()) {
            case HAND -> this.handItems.get(slot.getIndex());
            case HUMANOID_ARMOR -> this.armorItems.get(slot.getIndex());
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void setItemSlot(final EquipmentSlot slot, final ItemStack stack) {
        this.verifyEquippedItem(stack);
        switch (slot.getType()) {
            case HAND -> this.onEquipItem(slot, this.handItems.set(slot.getIndex(), stack), stack);
            case HUMANOID_ARMOR -> this.onEquipItem(slot, this.armorItems.set(slot.getIndex(), stack), stack);
            default -> { }
        }
    }

    @Override
    public void addAdditionalSaveData(final CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.store(tag, PROFILE_FIELD, ResolvableProfile.CODEC, this.getProfile());
        this.store(tag, HIDDEN_LAYERS_FIELD, LAYERS_CODEC, this.entityData.get(DATA_PLAYER_MODE_CUSTOMISATION));
        tag.putString(MAIN_HAND_FIELD, this.getMainArm() == HumanoidArm.LEFT ? "left" : "right");
        this.store(tag, POSE_FIELD, POSE_CODEC, this.getPose());
        tag.putBoolean(IMMOVABLE_FIELD, this.getImmovable());
        tag.putFloat(WALK_SPEED_FIELD, this.getWalkSpeed());

        final Component description = this.getDescription();
        if (description != null) {
            if (!description.equals(DEFAULT_DESCRIPTION)) {
                this.store(tag, DESCRIPTION_FIELD, ComponentSerialization.CODEC, description);
            }
        } else {
            tag.putBoolean(HIDE_DESCRIPTION_FIELD, true);
        }

        final ListTag armor = new ListTag();
        for (final ItemStack stack : this.armorItems) {
            armor.add(stack.saveOptional(this.level().registryAccess()));
        }
        tag.put("ArmorItems", armor);

        final ListTag hands = new ListTag();
        for (final ItemStack stack : this.handItems) {
            hands.add(stack.saveOptional(this.level().registryAccess()));
        }
        tag.put("HandItems", hands);
    }

    @Override
    public void readAdditionalSaveData(final CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.read(tag, PROFILE_FIELD, ResolvableProfile.CODEC).ifPresent(this::setProfile);
        this.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION,
                this.read(tag, HIDDEN_LAYERS_FIELD, LAYERS_CODEC).orElse(ALL_LAYERS));
        this.setMainArm(tag.contains(MAIN_HAND_FIELD, Tag.TAG_STRING)
                ? ("left".equals(tag.getString(MAIN_HAND_FIELD)) ? HumanoidArm.LEFT : HumanoidArm.RIGHT)
                : DEFAULT_MAIN_HAND);
        this.setPose(this.read(tag, POSE_FIELD, POSE_CODEC).orElse(Pose.STANDING));
        this.setImmovable(tag.getBoolean(IMMOVABLE_FIELD));
        this.setHideDescription(tag.getBoolean(HIDE_DESCRIPTION_FIELD));
        this.setDescription(this.read(tag, DESCRIPTION_FIELD, ComponentSerialization.CODEC).orElse(DEFAULT_DESCRIPTION));
        this.setWalkSpeed(tag.contains(WALK_SPEED_FIELD, Tag.TAG_ANY_NUMERIC) ? tag.getFloat(WALK_SPEED_FIELD) : 0.0f);

        if (tag.contains("ArmorItems", Tag.TAG_LIST)) {
            final ListTag armor = tag.getList("ArmorItems", Tag.TAG_COMPOUND);
            for (int i = 0; i < this.armorItems.size(); i++) {
                this.armorItems.set(i, ItemStack.parseOptional(this.level().registryAccess(), armor.getCompound(i)));
            }
        }
        if (tag.contains("HandItems", Tag.TAG_LIST)) {
            final ListTag hands = tag.getList("HandItems", Tag.TAG_COMPOUND);
            for (int i = 0; i < this.handItems.size(); i++) {
                this.handItems.set(i, ItemStack.parseOptional(this.level().registryAccess(), hands.getCompound(i)));
            }
        }
    }

    private <T> void store(final CompoundTag tag, final String key, final Codec<T> codec, final T value) {
        final RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, this.level().registryAccess());
        codec.encodeStart(ops, value)
                .resultOrPartial(err -> LibLib.getLogger().error("Failed to encode mannequin field '{}': {}", key, err))
                .ifPresent(encoded -> tag.put(key, encoded));
    }

    private <T> Optional<T> read(final CompoundTag tag, final String key, final Codec<T> codec) {
        if (!tag.contains(key)) {
            return Optional.empty();
        }
        final RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, this.level().registryAccess());
        return codec.parse(ops, tag.get(key))
                .resultOrPartial(err -> LibLib.getLogger().error("Failed to decode mannequin field '{}': {}", key, err));
    }

    private static byte computeAllLayers() {
        int mask = 0;
        for (final PlayerModelPart part : PlayerModelPart.values()) {
            mask |= part.getMask();
        }
        return (byte) mask;
    }

    private static DataResult<Pose> poseByName(final String name) {
        try {
            final Pose pose = Pose.valueOf(name.toUpperCase(Locale.ROOT));
            return VALID_POSES.contains(pose)
                    ? DataResult.success(pose)
                    : DataResult.error(() -> "Invalid pose: " + name);
        } catch (final IllegalArgumentException e) {
            return DataResult.error(() -> "Unknown pose: " + name);
        }
    }

    private static DataResult<PlayerModelPart> partById(final String id) {
        for (final PlayerModelPart part : PlayerModelPart.values()) {
            if (part.getId().equals(id)) {
                return DataResult.success(part);
            }
        }
        return DataResult.error(() -> "Unknown model part: " + id);
    }


}
