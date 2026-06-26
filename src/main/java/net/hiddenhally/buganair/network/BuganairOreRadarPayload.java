package net.hiddenhally.buganair.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BuganairOreRadarPayload(boolean isSearching) implements CustomPayload {
    public static final Id<BuganairOreRadarPayload> ID = new Id<>(Identifier.of("buganair", "ore_radar"));
    public static final PacketCodec<RegistryByteBuf, BuganairOreRadarPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, BuganairOreRadarPayload::isSearching,
            BuganairOreRadarPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}