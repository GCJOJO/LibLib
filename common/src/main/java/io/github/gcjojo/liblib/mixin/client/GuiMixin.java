package io.github.gcjojo.liblib.mixin.client;

import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.client.FadeManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void liblib$renderHead(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci)
    {
        if(LibLib.isHideHud())
        {
            FadeManager.renderFade(guiGraphics);
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void liblib$renderTail(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci)
    {
        FadeManager.renderFade(guiGraphics);
    }
}
