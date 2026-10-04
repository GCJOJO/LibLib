package io.github.gcjojo.liblib.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import io.github.gcjojo.liblib.LibLib;
import io.github.gcjojo.liblib.client.CustomCameraManager;
import io.github.gcjojo.liblib.tween.Easing;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
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

    public static void registerPayloadTypes() {
        if (Platform.getEnv() == EnvType.CLIENT) return;

        NetworkManager.registerS2CPayloadType(SendCameraTargetPayload.TYPE, SendCameraTargetPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(SendCameraMovementPayload.TYPE, SendCameraMovementPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(SendCameraPosTargetPayload.TYPE, SendCameraPosTargetPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(SendCameraRotTargetPayload.TYPE, SendCameraRotTargetPayload.STREAM_CODEC);

        NetworkManager.registerS2CPayloadType(HideHudPayload.TYPE, HideHudPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(LockInputPayload.TYPE, LockInputPayload.STREAM_CODEC);

        NetworkManager.registerS2CPayloadType(SetFovPayload.TYPE, SetFovPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(SetFovVariationPayload.TYPE, SetFovVariationPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(ClearFovPayload.TYPE, ClearFovPayload.STREAM_CODEC);
    }

    public static void registerClientReceiver() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ClearCameraPayload.TYPE, ClearCameraPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setActive(false);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SendCameraMovementPayload.TYPE, SendCameraMovementPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setCameraMovement(payload.oldPos, payload.oldRot, payload.newPos, payload.newRot, payload.easing, payload.easeTime);
                CustomCameraManager.setActive(true);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SendCameraTargetPayload.TYPE, SendCameraTargetPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setCameraTarget(payload.newPos, payload.newRot, payload.easing, payload.easeTime);
                CustomCameraManager.setActive(true);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SendCameraPosTargetPayload.TYPE, SendCameraPosTargetPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setPositionTarget(payload.newPos, payload.easing, payload.easeTime);
                CustomCameraManager.setActive(true);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SendCameraRotTargetPayload.TYPE, SendCameraRotTargetPayload.STREAM_CODEC, (payload, context) -> {
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
                CustomCameraManager.setFovVariation(payload.oldFov, payload.newFov, payload.easing, payload.easeTime);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ClearFovPayload.TYPE, ClearFovPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {

            });
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

    public record SendCameraPosTargetPayload(Vec3 newPos, Easing easing, float easeTime) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SendCameraPosTargetPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "send_camera_pos_target"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SendCameraPosTargetPayload> STREAM_CODEC = StreamCodec.composite(
                VEC3_STREAM_CODEC, SendCameraPosTargetPayload::newPos,
                EasingStreamCodec.CODEC, SendCameraPosTargetPayload::easing,
                ByteBufCodecs.FLOAT, SendCameraPosTargetPayload::easeTime,
                SendCameraPosTargetPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SendCameraRotTargetPayload(Vector3f newRot, Easing easing, float easeTime) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SendCameraRotTargetPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "send_camera_rot_target"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SendCameraRotTargetPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VECTOR3F, SendCameraRotTargetPayload::newRot,
                EasingStreamCodec.CODEC, SendCameraRotTargetPayload::easing,
                ByteBufCodecs.FLOAT, SendCameraRotTargetPayload::easeTime,
                SendCameraRotTargetPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SendCameraTargetPayload(Vec3 newPos, Vector3f newRot, Easing easing, float easeTime) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SendCameraTargetPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "send_camera_target"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SendCameraTargetPayload> STREAM_CODEC = StreamCodec.composite(
                VEC3_STREAM_CODEC, SendCameraTargetPayload::newPos,
                ByteBufCodecs.VECTOR3F, SendCameraTargetPayload::newRot,
                EasingStreamCodec.CODEC, SendCameraTargetPayload::easing,
                ByteBufCodecs.FLOAT, SendCameraTargetPayload::easeTime,
                SendCameraTargetPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SendCameraMovementPayload(Vec3 oldPos, Vector3f oldRot, Vec3 newPos, Vector3f newRot, Easing easing, float easeTime) implements CustomPacketPayload {
        public static final Type<SendCameraMovementPayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "send_camera_movement"));

        public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Vec3::x,
                ByteBufCodecs.DOUBLE, Vec3::y,
                ByteBufCodecs.DOUBLE, Vec3::z,
                Vec3::new
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SendCameraMovementPayload> STREAM_CODEC = StreamCodec.composite(
                VEC3_STREAM_CODEC, SendCameraMovementPayload::oldPos,
                ByteBufCodecs.VECTOR3F, SendCameraMovementPayload::oldRot,
                VEC3_STREAM_CODEC, SendCameraMovementPayload::newPos,
                ByteBufCodecs.VECTOR3F, SendCameraMovementPayload::newRot,
                EasingStreamCodec.CODEC, SendCameraMovementPayload::easing,
                ByteBufCodecs.FLOAT, SendCameraMovementPayload::easeTime,
                SendCameraMovementPayload::new
        );

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
