package io.github.gcjojo.liblib.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import io.github.gcjojo.liblib.CameraChunkLoader;
import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.client.CameraEffect;
import io.github.gcjojo.liblib.client.CameraEffects;
import io.github.gcjojo.liblib.client.CustomCameraManager;
import io.github.gcjojo.liblib.client.FadeManager;
import io.github.gcjojo.liblib.commands.CommandHelpers;
import io.github.gcjojo.liblib.math.Color;
import io.github.gcjojo.liblib.tween.Easing;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Map;
import java.util.stream.Collectors;

public class LibLibNetwork {

    public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, Vec3::x,
            ByteBufCodecs.DOUBLE, Vec3::y,
            ByteBufCodecs.DOUBLE, Vec3::z,
            Vec3::new
    );

    public static final StreamCodec<ByteBuf, Color> COLOR_STREAM_CODEC = ByteBufCodecs.INT.map(Color::new, Color::getColorInt);

    public static void registerPayloadTypes() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ClientClearCustomCameraPayload.TYPE, ClientClearCustomCameraPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                ServerPlayer player = (ServerPlayer) context.getPlayer();
                if(player == null) return;

                CameraChunkLoader.resetPlayerChunkPosition(player);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, ClientCameraPosPayload.TYPE, ClientCameraPosPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                ServerPlayer player = (ServerPlayer) context.getPlayer();
                if(player == null) return;

                CameraChunkLoader.onClientPos(player, payload.position());
            });
        });

        if (Platform.getEnv() == EnvType.CLIENT) return;

        NetworkManager.registerS2CPayloadType(CameraTargetPayload.TYPE, CameraTargetPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(CameraMovementPayload.TYPE, CameraMovementPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(CameraPosTargetPayload.TYPE, CameraPosTargetPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(CameraRotTargetPayload.TYPE, CameraRotTargetPayload.STREAM_CODEC);

        NetworkManager.registerS2CPayloadType(HideHudPayload.TYPE, HideHudPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(LockInputPayload.TYPE, LockInputPayload.STREAM_CODEC);

        NetworkManager.registerS2CPayloadType(SetFovPayload.TYPE, SetFovPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(SetFovVariationPayload.TYPE, SetFovVariationPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(ClearFovPayload.TYPE, ClearFovPayload.STREAM_CODEC);

        NetworkManager.registerS2CPayloadType(FadePayload.TYPE, FadePayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(ClearFadePayload.TYPE, ClearFadePayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(ClearAllFadesPayload.TYPE, ClearAllFadesPayload.STREAM_CODEC);

        NetworkManager.registerS2CPayloadType(ShakePayload.TYPE, ShakePayload.STREAM_CODEC);
    }

    public static void registerClientReceiver() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ClearCameraPayload.TYPE, ClearCameraPayload.STREAM_CODEC, (payload, context) ->
            context.queue(CustomCameraManager::clearCustomCamera)
        );

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CameraMovementPayload.TYPE, CameraMovementPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setCameraMovement(payload.oldPos, payload.oldRot, payload.newPos, payload.newRot, payload.easing, payload.easeTime);
                CustomCameraManager.setActive(true);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CameraTargetPayload.TYPE, CameraTargetPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setCameraTarget(payload.newPos, payload.newRot, payload.easing, payload.easeTime);
                CustomCameraManager.setActive(true);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CameraPosTargetPayload.TYPE, CameraPosTargetPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setPositionTarget(payload.newPos, payload.easing, payload.easeTime);
                CustomCameraManager.setActive(true);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CameraRotTargetPayload.TYPE, CameraRotTargetPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setRotationTarget(payload.newRot, payload.easing, payload.easeTime);
                CustomCameraManager.setActive(true);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, HideHudPayload.TYPE, HideHudPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                switch(payload.element)
                {
                    case Hud -> LibLib.setHideHud(payload.hide);
                    case Hand -> LibLib.setHideHand(payload.hide);
                }
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, LockInputPayload.TYPE, LockInputPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                switch(payload.input)
                {
                    case Keyboard -> LibLib.setLockKeyboardInput(payload.lock);
                    case Mouse -> LibLib.setLockMouseInput(payload.lock);
                }
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SetFovPayload.TYPE, SetFovPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setTargetFov(payload.newFov, payload.easing, payload.easeTime);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SetFovVariationPayload.TYPE, SetFovVariationPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setFovFromTo(payload.oldFov, payload.newFov, payload.easing, payload.easeTime);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ClearFovPayload.TYPE, ClearFovPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> CustomCameraManager.clearFov(payload.easing, payload.easeTime));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, FadePayload.TYPE, FadePayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> FadeManager.fade(payload.layer, payload.startColor, payload.endColor, payload.easing, payload.easeTime));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ClearFadePayload.TYPE, ClearFadePayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> FadeManager.clearFade(payload.layer));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ClearAllFadesPayload.TYPE, ClearAllFadesPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(FadeManager::clearAllFade);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ShakePayload.TYPE, ShakePayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CameraEffect innerEffect = null;
                switch(payload.shakeType())
                {
                    case POSITIONAL -> innerEffect = CameraEffects.shakePosition(payload.intensity(), payload.speed());
                    case ROTATIONAL -> innerEffect = CameraEffects.shakeRotation(payload.intensity(), payload.speed());
                }
                if(innerEffect == null)
                    return;

                Enveloppe enveloppe = payload.enveloppe();
                CameraEffect enveloppeEffect = new CameraEffects.Enveloppe(innerEffect, enveloppe.attack(), enveloppe.duration(), enveloppe.decay());

                CustomCameraManager.setCameraEffect(payload.layer(), enveloppeEffect);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ClearShakePayload.TYPE, ClearShakePayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> CustomCameraManager.clearCameraEffect(payload.layer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ClearAllShakePayload.TYPE, ClearAllShakePayload.STREAM_CODEC, (payload, context) -> {
            context.queue(CustomCameraManager::clearCameraEffects);
        });
    }

    public record ClearCameraPayload() implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<ClearCameraPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "clear_camera"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ClearCameraPayload> STREAM_CODEC = StreamCodec.unit(new ClearCameraPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record CameraPosTargetPayload(Vec3 newPos, Easing easing, float easeTime) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<CameraPosTargetPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "send_camera_pos_target"));

        public static final StreamCodec<RegistryFriendlyByteBuf, CameraPosTargetPayload> STREAM_CODEC = StreamCodec.composite(
                VEC3_STREAM_CODEC, CameraPosTargetPayload::newPos,
                EasingStreamCodec.CODEC, CameraPosTargetPayload::easing,
                ByteBufCodecs.FLOAT, CameraPosTargetPayload::easeTime,
                CameraPosTargetPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record CameraRotTargetPayload(Vector3f newRot, Easing easing, float easeTime) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<CameraRotTargetPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "send_camera_rot_target"));

        public static final StreamCodec<RegistryFriendlyByteBuf, CameraRotTargetPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VECTOR3F, CameraRotTargetPayload::newRot,
                EasingStreamCodec.CODEC, CameraRotTargetPayload::easing,
                ByteBufCodecs.FLOAT, CameraRotTargetPayload::easeTime,
                CameraRotTargetPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record CameraTargetPayload(Vec3 newPos, Vector3f newRot, Easing easing, float easeTime) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<CameraTargetPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "send_camera_target"));

        public static final StreamCodec<RegistryFriendlyByteBuf, CameraTargetPayload> STREAM_CODEC = StreamCodec.composite(
                VEC3_STREAM_CODEC, CameraTargetPayload::newPos,
                ByteBufCodecs.VECTOR3F, CameraTargetPayload::newRot,
                EasingStreamCodec.CODEC, CameraTargetPayload::easing,
                ByteBufCodecs.FLOAT, CameraTargetPayload::easeTime,
                CameraTargetPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record CameraMovementPayload(Vec3 oldPos, Vector3f oldRot, Vec3 newPos, Vector3f newRot, Easing easing, float easeTime) implements CustomPacketPayload {
        public static final Type<CameraMovementPayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "send_camera_movement"));

        public static final StreamCodec<RegistryFriendlyByteBuf, CameraMovementPayload> STREAM_CODEC = StreamCodec.composite(
                VEC3_STREAM_CODEC, CameraMovementPayload::oldPos,
                ByteBufCodecs.VECTOR3F, CameraMovementPayload::oldRot,
                VEC3_STREAM_CODEC, CameraMovementPayload::newPos,
                ByteBufCodecs.VECTOR3F, CameraMovementPayload::newRot,
                EasingStreamCodec.CODEC, CameraMovementPayload::easing,
                ByteBufCodecs.FLOAT, CameraMovementPayload::easeTime,
                CameraMovementPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ClientCameraPosPayload(Vec3 position) implements CustomPacketPayload {
        public static final Type<ClientCameraPosPayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "client_camera_position"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientCameraPosPayload> STREAM_CODEC = StreamCodec.composite(
                VEC3_STREAM_CODEC, ClientCameraPosPayload::position,
                ClientCameraPosPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ClientClearCustomCameraPayload() implements CustomPacketPayload
    {
        public static final Type<ClientClearCustomCameraPayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "client_clear_custom_camera"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientClearCustomCameraPayload> STREAM_CODEC = StreamCodec.unit(new ClientClearCustomCameraPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record HideHudPayload(HideHudElement element, boolean hide) implements CustomPacketPayload {

        public enum HideHudElement {
            Hud,
            Hand
        }

        public static final StreamCodec<ByteBuf, HideHudElement> HIDE_HUD_ENUM_CODEC =
                ByteBufCodecs.idMapper(id -> HideHudElement.values()[id], HideHudElement::ordinal);

        public static final Type<HideHudPayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "hide_hud"));

        public static final StreamCodec<RegistryFriendlyByteBuf, HideHudPayload> STREAM_CODEC = StreamCodec.composite(
                HIDE_HUD_ENUM_CODEC, HideHudPayload::element,
                ByteBufCodecs.BOOL, HideHudPayload::hide,
                HideHudPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record LockInputPayload(InputType input, boolean lock) implements CustomPacketPayload {

        public enum InputType {
            Keyboard,
            Mouse
        }

        public static final StreamCodec<ByteBuf, InputType> INPUT_TYPE_STREAM_CODEC =
                ByteBufCodecs.idMapper(id -> InputType.values()[id], InputType::ordinal);

        public static final Type<LockInputPayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "lock_input"));

        public static final StreamCodec<RegistryFriendlyByteBuf, LockInputPayload> STREAM_CODEC = StreamCodec.composite(
                INPUT_TYPE_STREAM_CODEC, LockInputPayload::input,
                ByteBufCodecs.BOOL, LockInputPayload::lock,
                LockInputPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SetFovPayload(double newFov, Easing easing, float easeTime) implements CustomPacketPayload
    {
        public static final Type<SetFovPayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "set_fov"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SetFovPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, SetFovPayload::newFov,
                EasingStreamCodec.CODEC, SetFovPayload::easing,
                ByteBufCodecs.FLOAT, SetFovPayload::easeTime,
                SetFovPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SetFovVariationPayload(double oldFov, double newFov, Easing easing, float easeTime) implements CustomPacketPayload {
        public static final Type<SetFovVariationPayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "set_fov_variation"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SetFovVariationPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, SetFovVariationPayload::oldFov,
                ByteBufCodecs.DOUBLE, SetFovVariationPayload::newFov,
                EasingStreamCodec.CODEC, SetFovVariationPayload::easing,
                ByteBufCodecs.FLOAT, SetFovVariationPayload::easeTime,
                SetFovVariationPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ClearFovPayload(Easing easing, float easeTime) implements CustomPacketPayload {
        public static final Type<ClearFovPayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "clear_fov"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ClearFovPayload> STREAM_CODEC = StreamCodec.composite(
                EasingStreamCodec.CODEC, ClearFovPayload::easing,
                ByteBufCodecs.FLOAT, ClearFovPayload::easeTime,
                ClearFovPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record FadePayload(int layer, Color startColor, Color endColor, Easing easing, float easeTime) implements CustomPacketPayload
    {
        public static final Type<FadePayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "fade"));

        public static final StreamCodec<RegistryFriendlyByteBuf, FadePayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, FadePayload::layer,
                COLOR_STREAM_CODEC, FadePayload::startColor,
                COLOR_STREAM_CODEC, FadePayload::endColor,
                EasingStreamCodec.CODEC, FadePayload::easing,
                ByteBufCodecs.FLOAT, FadePayload::easeTime,
                FadePayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ClearFadePayload(int layer) implements CustomPacketPayload
    {
        public static final Type<ClearFadePayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "clear_fade"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ClearFadePayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, ClearFadePayload::layer,
                ClearFadePayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ClearAllFadesPayload() implements CustomPacketPayload
    {
        public static final Type<ClearAllFadesPayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "clear_all_fade"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ClearAllFadesPayload> STREAM_CODEC = StreamCodec.unit(new ClearAllFadesPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record Enveloppe(float duration, float attack, float decay)
    {
        public static final StreamCodec<ByteBuf, Enveloppe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.FLOAT, Enveloppe::duration,
                ByteBufCodecs.FLOAT, Enveloppe::attack,
                ByteBufCodecs.FLOAT, Enveloppe::decay,
                Enveloppe::new
        );
    }

    public record ShakePayload(CommandHelpers.ShakeType shakeType, int layer, float intensity, float speed, Enveloppe enveloppe) implements CustomPacketPayload
    {
        public static final StreamCodec<ByteBuf, CommandHelpers.ShakeType> SHAKE_TYPE_ENUM_CODEC =
                ByteBufCodecs.idMapper(id -> CommandHelpers.ShakeType.values()[id], CommandHelpers.ShakeType::ordinal);

        public static final Type<ShakePayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "set_shake"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ShakePayload> STREAM_CODEC = StreamCodec.composite(
                SHAKE_TYPE_ENUM_CODEC, ShakePayload::shakeType,
                ByteBufCodecs.INT, ShakePayload::layer,
                ByteBufCodecs.FLOAT, ShakePayload::intensity,
                ByteBufCodecs.FLOAT, ShakePayload::speed,
                Enveloppe.STREAM_CODEC, ShakePayload::enveloppe,
                ShakePayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ClearShakePayload(int layer) implements CustomPacketPayload {
        public static final Type<ClearShakePayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "clear_shake"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ClearShakePayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, ClearShakePayload::layer,
                ClearShakePayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public record ClearAllShakePayload() implements CustomPacketPayload {
        public static final Type<ClearAllShakePayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "clear_all_shake"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ClearAllShakePayload> STREAM_CODEC = StreamCodec.unit(new ClearAllShakePayload());

        @Override
        public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }

    public static class EasingStreamCodec {
        private static final Map<String, Easing> BY_NAME = Map.ofEntries(
                Map.entry("linear", Easing.LINEAR),

                Map.entry("in_sine", Easing.Sine.EASE_IN),
                Map.entry("out_sine", Easing.Sine.EASE_OUT),
                Map.entry("in_out_sine", Easing.Sine.EASE_IN_OUT),

                Map.entry("in_cubic", Easing.Cubic.EASE_IN),
                Map.entry("out_cubic", Easing.Cubic.EASE_OUT),
                Map.entry("in_out_cubic", Easing.Cubic.EASE_IN_OUT),

                Map.entry("in_quad", Easing.Quad.EASE_IN),
                Map.entry("out_quad", Easing.Quad.EASE_OUT),
                Map.entry("in_out_quad", Easing.Quad.EASE_IN_OUT),

                Map.entry("in_back", Easing.Back.EASE_IN),
                Map.entry("out_back", Easing.Back.EASE_OUT),
                Map.entry("in_out_back", Easing.Back.EASE_IN_OUT),

                Map.entry("in_bounce", Easing.Bounce.EASE_IN),
                Map.entry("out_bounce", Easing.Bounce.EASE_OUT),
                Map.entry("in_out_bounce", Easing.Bounce.EASE_IN_OUT)
        );
        private static final Map<Easing, String> BY_VALUE = BY_NAME.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        public static final StreamCodec<ByteBuf, Easing> CODEC = StreamCodec.of(
                (buf, easing) -> ByteBufCodecs.STRING_UTF8.encode(buf, BY_VALUE.getOrDefault(easing, "linear")),
                buf -> BY_NAME.getOrDefault(ByteBufCodecs.STRING_UTF8.decode(buf), Easing.LINEAR)
        );
    }
}
