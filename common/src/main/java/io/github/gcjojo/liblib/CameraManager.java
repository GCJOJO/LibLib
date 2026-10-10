package io.github.gcjojo.liblib;

import dev.architectury.networking.NetworkManager;
import io.github.gcjojo.liblib.commands.CommandHelpers;
import io.github.gcjojo.liblib.network.LibLibNetwork;
import io.github.gcjojo.liblib.tween.Easing;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Collection;

public class CameraManager {

    public static void clearCamera(ServerPlayer serverPlayer) {
        NetworkManager.sendToPlayer(serverPlayer, new LibLibNetwork.ClearCameraPayload());
    }

    public static void clearCamera(Collection<ServerPlayer> serverPlayer) {
        NetworkManager.sendToPlayers(serverPlayer, new LibLibNetwork.ClearCameraPayload());
    }

    public static void setCameraPosition(ServerPlayer serverPlayer, Vec3 cameraPos) {
        setCameraPosition(serverPlayer, cameraPos, Easing.LINEAR, 0);
    }

    public static void setCameraPosition(Collection<ServerPlayer> serverPlayers, Vec3 cameraPos) {
        setCameraPosition(serverPlayers, cameraPos, Easing.LINEAR, 0);
    }

    public static void setCameraPosition(ServerPlayer serverPlayer, Vec3 cameraPos, Easing easing, float easeTime) {
        NetworkManager.sendToPlayer(serverPlayer, new LibLibNetwork.CameraPosTargetPayload(cameraPos, easing, easeTime));
    }

    public static void setCameraPosition(Collection<ServerPlayer> serverPlayers, Vec3 cameraPos, Easing easing, float easeTime) {
        NetworkManager.sendToPlayers(serverPlayers, new LibLibNetwork.CameraPosTargetPayload(cameraPos, easing, easeTime));
    }

    public static void setCameraRotation(ServerPlayer serverPlayer, Vector3f cameraRot) {
        setCameraRotation(serverPlayer, cameraRot, Easing.LINEAR, 0);
    }

    public static void setCameraRotation(Collection<ServerPlayer> serverPlayers, Vector3f cameraRot) {
        setCameraRotation(serverPlayers, cameraRot, Easing.LINEAR, 0);
    }

    public static void setCameraRotation(ServerPlayer serverPlayer, Vector3f cameraRot, Easing easing, float easeTime) {
        NetworkManager.sendToPlayer(serverPlayer, new LibLibNetwork.CameraRotTargetPayload(cameraRot, easing, easeTime));
    }

    public static void setCameraRotation(Collection<ServerPlayer> serverPlayers, Vector3f cameraRot, Easing easing, float easeTime) {
        NetworkManager.sendToPlayers(serverPlayers, new LibLibNetwork.CameraRotTargetPayload(cameraRot, easing, easeTime));
    }

    public static void setCameraTarget(ServerPlayer serverPlayer, Vec3 newPos, Vector3f newRot) {
        setCameraTarget(serverPlayer, newPos, newRot, Easing.LINEAR, 0);
    }

    public static void setCameraTarget(Collection<ServerPlayer> serverPlayers, Vec3 newPos, Vector3f newRot) {
        setCameraTarget(serverPlayers, newPos, newRot, Easing.LINEAR, 0);
    }

    public static void setCameraTarget(ServerPlayer serverPlayer, Vec3 newPos, Vector3f newRot, Easing easing, float easeTime) {
        NetworkManager.sendToPlayer(serverPlayer, new LibLibNetwork.CameraTargetPayload(newPos, newRot, easing, easeTime));
    }

    public static void setCameraTarget(Collection<ServerPlayer> serverPlayers, Vec3 newPos, Vector3f newRot, Easing easing, float easeTime) {
        NetworkManager.sendToPlayers(serverPlayers, new LibLibNetwork.CameraTargetPayload(newPos, newRot, easing, easeTime));
    }

    public static void setCameraMovement(ServerPlayer serverPlayer, Vec3 oldPos, Vector3f oldRot, Vec3 newPos, Vector3f newRot, Easing easing, float easeTime ) {
        NetworkManager.sendToPlayer(serverPlayer, new LibLibNetwork.CameraMovementPayload(oldPos, oldRot, newPos, newRot, easing, easeTime));
    }

    public static void setCameraMovement(Collection<ServerPlayer> serverPlayers, Vec3 oldPos, Vector3f oldRot, Vec3 newPos, Vector3f newRot, Easing easing, float easeTime ) {
        NetworkManager.sendToPlayers(serverPlayers, new LibLibNetwork.CameraMovementPayload(oldPos, oldRot, newPos, newRot, easing, easeTime));
    }

    public static void setFov(ServerPlayer serverPlayer, double fov) {
        setFov(serverPlayer, fov, Easing.LINEAR, 0);
    }

    public static void setFov(Collection<ServerPlayer> serverPlayers, double fov) {
        setFov(serverPlayers, fov, Easing.LINEAR, 0);
    }

    public static void setFov(ServerPlayer serverPlayer, double fov, Easing easing, float easeTime) {
        NetworkManager.sendToPlayer(serverPlayer, new LibLibNetwork.SetFovPayload(fov, easing, easeTime));
    }

    public static void setFov(Collection<ServerPlayer> serverPlayers, double fov, Easing easing, float easeTime) {
        NetworkManager.sendToPlayers(serverPlayers, new LibLibNetwork.SetFovPayload(fov, easing, easeTime));
    }

    public static void setFov(ServerPlayer serverPlayer, double oldFov, double newFov, Easing easing, float easeTime) {
        NetworkManager.sendToPlayer(serverPlayer, new LibLibNetwork.SetFovVariationPayload(oldFov, newFov, easing, easeTime));
    }

    public static void setFov(Collection<ServerPlayer> serverPlayers, double oldFov, double newFov, Easing easing, float easeTime) {
        NetworkManager.sendToPlayers(serverPlayers, new LibLibNetwork.SetFovVariationPayload(oldFov, newFov, easing, easeTime));
    }

    public static void clearFov(ServerPlayer serverPlayer) {
        clearFov(serverPlayer, Easing.LINEAR, 0);
    }

    public static void clearFov(Collection<ServerPlayer> serverPlayers) {
        clearFov(serverPlayers, Easing.LINEAR, 0);
    }

    public static void clearFov(ServerPlayer serverPlayer, Easing easing, float easeTime) {
        NetworkManager.sendToPlayer(serverPlayer, new LibLibNetwork.ClearFovPayload(easing, easeTime));
    }

    public static void clearFov(Collection<ServerPlayer> serverPlayers, Easing easing, float easeTime) {
        NetworkManager.sendToPlayers(serverPlayers, new LibLibNetwork.ClearFovPayload(easing, easeTime));
    }

    public static void shake(ServerPlayer serverPlayer, int layer, CommandHelpers.ShakeType shakeType, float intensity, float speed, float duration, float attack, float decay) {
        LibLibNetwork.ShakePayload payload = new LibLibNetwork.ShakePayload(shakeType, layer, intensity, speed, new LibLibNetwork.Enveloppe(duration, attack, decay));
        NetworkManager.sendToPlayer(serverPlayer, payload);
    }

    public static void shake(Collection<ServerPlayer> serverPlayers, int layer, CommandHelpers.ShakeType shakeType, float intensity, float speed, float duration, float attack, float decay) {
        LibLibNetwork.ShakePayload payload = new LibLibNetwork.ShakePayload(shakeType, layer, intensity, speed, new LibLibNetwork.Enveloppe(duration, attack, decay));
        NetworkManager.sendToPlayers(serverPlayers, payload);
    }

    public static void clearShake(ServerPlayer serverPlayer, int layer) {
        NetworkManager.sendToPlayer(serverPlayer, new LibLibNetwork.ClearShakePayload(layer));
    }

    public static void clearShake(Collection<ServerPlayer> serverPlayers, int layer) {
        NetworkManager.sendToPlayers(serverPlayers, new LibLibNetwork.ClearShakePayload(layer));
    }

    public static void clearAllShake(ServerPlayer serverPlayer) {
        NetworkManager.sendToPlayer(serverPlayer, new LibLibNetwork.ClearAllShakePayload());
    }

    public static void clearAllShake(Collection<ServerPlayer> serverPlayers) {
        NetworkManager.sendToPlayers(serverPlayers, new LibLibNetwork.ClearAllShakePayload());
    }

    public static void hideHud(ServerPlayer serverPlayer, LibLibNetwork.HideHudPayload.HideHudElement hideHudElement, boolean hide) {
        NetworkManager.sendToPlayer(serverPlayer, new LibLibNetwork.HideHudPayload(hideHudElement, hide));
    }

    public static void hideHud(Collection<ServerPlayer> serverPlayers, LibLibNetwork.HideHudPayload.HideHudElement hideHudElement, boolean hide) {
        NetworkManager.sendToPlayers(serverPlayers, new LibLibNetwork.HideHudPayload(hideHudElement, hide));
    }
}
