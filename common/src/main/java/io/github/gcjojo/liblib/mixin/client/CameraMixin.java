package io.github.gcjojo.liblib.mixin.client;

import io.github.gcjojo.liblib.client.CameraEffect;
import io.github.gcjojo.liblib.client.ClientCameraManager;
import io.github.gcjojo.liblib.utils.MathUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    void liblib$setRotation(float pitch, float yaw, float roll) {
        this.xRot = MathUtils.toRad(pitch);
        this.yRot = MathUtils.toRad(yaw);
        this.liblib$zRot = MathUtils.toRad(roll);

        this.rotation.rotationYXZ((float) Math.PI - this.yRot, -this.xRot, this.liblib$zRot);
        FORWARDS.rotate(this.rotation, this.forwards);
        UP.rotate(this.rotation, this.up);
        LEFT.rotate(this.rotation, this.left);
    }

    @Unique
    void liblib$setRotation(Vector3f rotation) {
        this.liblib$setRotation(rotation.x, rotation.y, rotation.z);
    }

    @Shadow
    protected abstract void setPosition(Vec3 position);

    @Shadow
    private Vec3 position;

    @Inject(method = "setup", at = @At("RETURN"))
    private void liblib$overrideCamera(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse, float partialTick, CallbackInfo ci) {
        if (!ClientCameraManager.isActive()) {
            ClientCameraManager.setPosition(this.position);
            ClientCameraManager.setRotation(new Vector3f(this.xRot, this.yRot, this.liblib$zRot));
        }

        if(!ClientCameraManager.isActive() && !ClientCameraManager.hasAnyEffect())
            return;

        Vec3 cameraPos = ClientCameraManager.getPosition();
        Vector3f cameraRot = ClientCameraManager.getRotation();

        CameraEffect.CameraTransform offsets = ClientCameraManager.computeOffsets();

        Vec3 finalCameraPos = cameraPos.add(offsets.positionOffset());

        this.setPosition(finalCameraPos);
        this.liblib$setRotation(cameraRot.add(offsets.rotationOffset(), new Vector3f()));

        Minecraft.getInstance().levelRenderer.needsUpdate();
    }

    @Inject(method = "isDetached", at = @At("HEAD"), cancellable = true)
    private void liblib$isDetached(CallbackInfoReturnable<Boolean> cir)
    {
        if(ClientCameraManager.isActive())
            cir.setReturnValue(true);
    }
}
