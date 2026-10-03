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

    public class EasingStreamCodec
    {
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

    public static void registerPayloadTypes() {
        if (Platform.getEnv() == EnvType.CLIENT) return;

        NetworkManager.registerS2CPayloadType(SendCameraPayload.TYPE, SendCameraPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(SendCameraFromPayload.TYPE, SendCameraFromPayload.STREAM_CODEC);
    }

    public static void registerClientReceiver()
    {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, ClearCameraPayload.TYPE, ClearCameraPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setActive(false);
            });
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SendCameraPayload.TYPE, SendCameraPayload.STREAM_CODEC, (payload, context) -> {
            context.queue(() -> {
                CustomCameraManager.setCameraTarget(payload.newPos, payload.newRot, payload.easing, payload.easeTime);
                CustomCameraManager.setActive(true);
            });
        });
    }

    public record ClearCameraPayload() implements CustomPacketPayload
    {
        public static final CustomPacketPayload.Type<ClearCameraPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "clear_camera"));

        public static final StreamCodec<RegistryFriendlyByteBuf, ClearCameraPayload> STREAM_CODEC = StreamCodec.unit(new ClearCameraPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SendCameraPayload(Vec3 newPos, Vector3f newRot, Easing easing, float easeTime) implements CustomPacketPayload
    {
        public static final CustomPacketPayload.Type<SendCameraPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "send_camera"));

        public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Vec3::x,
                ByteBufCodecs.DOUBLE, Vec3::y,
                ByteBufCodecs.DOUBLE, Vec3::z,
                Vec3::new
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SendCameraPayload> STREAM_CODEC = StreamCodec.composite(
                VEC3_STREAM_CODEC, SendCameraPayload::newPos,
                ByteBufCodecs.VECTOR3F, SendCameraPayload::newRot,
                EasingStreamCodec.CODEC, SendCameraPayload::easing,
                ByteBufCodecs.FLOAT, SendCameraPayload::easeTime,
                SendCameraPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SendCameraFromPayload(Vec3 oldPos, Vector3f oldRot, Vec3 newPos, Vector3f newRot, Easing easing, float easeTime) implements CustomPacketPayload {
        public static final Type<SendCameraFromPayload> TYPE = new Type<>(ResourceLocation.tryBuild(LibLib.MOD_ID, "send_camera_from"));

        public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Vec3::x,
                ByteBufCodecs.DOUBLE, Vec3::y,
                ByteBufCodecs.DOUBLE, Vec3::z,
                Vec3::new
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, SendCameraFromPayload> STREAM_CODEC = StreamCodec.composite(
                VEC3_STREAM_CODEC, SendCameraFromPayload::oldPos,
                ByteBufCodecs.VECTOR3F, SendCameraFromPayload::oldRot,
                VEC3_STREAM_CODEC, SendCameraFromPayload::newPos,
                ByteBufCodecs.VECTOR3F, SendCameraFromPayload::newRot,
                EasingStreamCodec.CODEC, SendCameraFromPayload::easing,
                ByteBufCodecs.FLOAT, SendCameraFromPayload::easeTime,
                SendCameraFromPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
