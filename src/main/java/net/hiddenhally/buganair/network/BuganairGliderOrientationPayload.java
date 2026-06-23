package net.hiddenhally.buganair.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BuganairGliderOrientationPayload(float pitch, float yaw, float roll) implements CustomPayload {
    public static final CustomPayload.Id<BuganairGliderOrientationPayload> ID =
            new CustomPayload.Id<>(Identifier.of("buganair", "glider_orientation"));

    public static final PacketCodec<RegistryByteBuf, BuganairGliderOrientationPayload> CODEC = PacketCodec.ofStatic(
            (buf, payload) -> {
                buf.writeFloat(payload.pitch());
                buf.writeFloat(payload.yaw());
                buf.writeFloat(payload.roll());
            },
            buf -> new BuganairGliderOrientationPayload(buf.readFloat(), buf.readFloat(), buf.readFloat())
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}