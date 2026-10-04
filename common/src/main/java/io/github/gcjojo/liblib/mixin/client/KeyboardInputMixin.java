package io.github.gcjojo.liblib.mixin.client;

import io.github.gcjojo.liblib.LibLib;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardInputMixin {

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    public void liblib$keyPress(long l, int i, int j, int k, int m, CallbackInfo ci)
    {
        if(LibLib.isLockKeyboardInput())
            ci.cancel();
    }
}
