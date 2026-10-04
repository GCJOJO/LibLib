package io.github.gcjojo.liblib.mixin.client;

import io.github.gcjojo.liblib.LibLib;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void liblib$onRenderHand(Camera camera, float f, Matrix4f matrix4f, CallbackInfo ci)
    {
        if(LibLib.isHideHand())
            ci.cancel();
    }

    @Shadow
    public abstract void setRenderHand(boolean bl);
    @Shadow
    public abstract void setRenderBlockOutline(boolean bl);

    @Inject(method = "tick", at = @At("HEAD"))
    private void liblib$tick(CallbackInfo ci)
    {
        setRenderHand(!LibLib.isHideHand());
        setRenderBlockOutline(!LibLib.isHideHud());
    }
}
