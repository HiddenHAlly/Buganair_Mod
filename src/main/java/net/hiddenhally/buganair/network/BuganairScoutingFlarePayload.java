package net.hiddenhally.buganair.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BuganairScoutingFlarePayload(boolean isScouting) implements CustomPayload {
    public static final Id<BuganairScoutingFlarePayload> ID = new Id<>(Identifier.of("buganair", "scouting_flare"));
    public static final PacketCodec<RegistryByteBuf, BuganairScoutingFlarePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, BuganairScoutingFlarePayload::isScouting,
            BuganairScoutingFlarePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}