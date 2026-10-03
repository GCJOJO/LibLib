package io.github.gcjojo.liblib.mixin.client;

import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.client.CustomCameraManager;
import io.github.gcjojo.liblib.utils.MathUtils;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    @Final
    private static Vector3f FORWARDS;
    @Shadow
    @Final
    private static Vector3f UP;
    @Shadow
    @Final
    private static Vector3f LEFT;
    @Shadow
    @Final
    private Vector3f forwards;
    @Shadow
    @Final
    private Vector3f up;
    @Shadow
    @Final
    private Vector3f left;
    @Shadow
    @Final
    private Quaternionf rotation;
    @Shadow
    private float xRot;
    @Shadow
    private float yRot;
    @Unique
    private float liblib$zRot = 0.0f;

    @Unique
    public float liblib$zRot() {
        return this.liblib$zRot;
    }

    @Unique
    void liblib$setRotation(float yaw, float pitch, float roll) {
        this.xRot = MathUtils.toRad(yaw);
        this.yRot = MathUtils.toRad(pitch);
        this.liblib$zRot = MathUtils.toRad(roll);

        this.rotation.rotationYXZ((float) Math.PI - this.yRot, -this.xRot, this.liblib$zRot);
        FORWARDS.rotate(this.rotation, this.forwards);
        UP.rotate(this.rotation, this.up);
        LEFT.rotate(this.rotation, this.left);
    }

    @Unique
    void liblib$setRotation(Vector3f rotation) {
        float yaw = rotation.x;
        float pitch = rotation.y;
        float roll = rotation.z;
        this.liblib$setRotation(yaw, pitch, roll);
    }

    @Shadow
    protected abstract void setPosition(Vec3 position);

    @Shadow
    private Vec3 position;

    @Inject(method = "setup", at = @At("RETURN"))
    private void liblib$overrideCamera(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        if (CustomCameraManager.isActive()) {
            this.setPosition(CustomCameraManager.getPosition());
            this.liblib$setRotation(CustomCameraManager.getRotation());
        } else {
            CustomCameraManager.setPosition(this.position);
            CustomCameraManager.setRotation(new Vector3f(this.xRot, this.yRot, this.liblib$zRot));
        }
    }
}
