package io.github.gcjojo.liblib.mixin.client;

import io.github.gcjojo.liblib.LibLib;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    public void liblib$turnPlayer(double d, CallbackInfo ci) {
        if(LibLib.isLockMouseInput())
            ci.cancel();
    }

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    public void liblib$onPress(long l, int i, int j, int k, CallbackInfo ci)
    {
        if(LibLib.isLockMouseInput())
            ci.cancel();
    }
}
