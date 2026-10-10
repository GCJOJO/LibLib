package io.github.gcjojo.liblib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.gcjojo.liblib.client.ClientCameraManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Claude Slop
@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @WrapOperation(method = "setupRender",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getX()D"))
    private double liblib$x(LocalPlayer player, Operation<Double> original) {
        return ClientCameraManager.isActive()
                ? Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().x
                : original.call(player);
    }

    @WrapOperation(method = "setupRender",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getY()D"))
    private double liblib$y(LocalPlayer player, Operation<Double> original) {
        return ClientCameraManager.isActive()
                ? Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y
                : original.call(player);
    }

    @WrapOperation(method = "setupRender",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getZ()D"))
    private double liblib$z(LocalPlayer player, Operation<Double> original) {
        return ClientCameraManager.isActive()
                ? Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().z
                : original.call(player);
    }
}
