package net.hiddenhally.buganair.network;

import net.hiddenhally.buganair.Buganair;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

// Now it carries the client's visual xOffset to the server
public record BuganairSniperFirePayload(float xOffset) implements CustomPayload {
    public static final CustomPayload.Id<BuganairSniperFirePayload> ID =
            new CustomPayload.Id<>(Identifier.of(Buganair.MOD_ID, "sniper_fire"));

    public static final PacketCodec<RegistryByteBuf, BuganairSniperFirePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, BuganairSniperFirePayload::xOffset,
            BuganairSniperFirePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}