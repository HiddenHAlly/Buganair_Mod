package net.hiddenhally.buganair.network;

import net.hiddenhally.buganair.Buganair;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BuganairBoatInputPayload(int entityId, int forward, int sideways, int vertical) implements CustomPayload {
    public static final CustomPayload.Id<BuganairBoatInputPayload> ID = new CustomPayload.Id<>(Identifier.of(Buganair.MOD_ID, "boat_input"));
    public static final PacketCodec<RegistryByteBuf, BuganairBoatInputPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.INTEGER,
        BuganairBoatInputPayload::entityId,
        PacketCodecs.INTEGER,
        BuganairBoatInputPayload::forward,
        PacketCodecs.INTEGER,
        BuganairBoatInputPayload::sideways,
        PacketCodecs.INTEGER,
        BuganairBoatInputPayload::vertical,
        BuganairBoatInputPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
