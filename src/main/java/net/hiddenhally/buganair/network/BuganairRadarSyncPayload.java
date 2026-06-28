package net.hiddenhally.buganair.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static net.hiddenhally.buganair.Buganair.MOD_ID;

public record BuganairRadarSyncPayload(BlockPos center) implements CustomPayload {
    public static final CustomPayload.Id<BuganairRadarSyncPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "radar_sync"));

    public static final PacketCodec<RegistryByteBuf, BuganairRadarSyncPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, BuganairRadarSyncPayload::center,
            BuganairRadarSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}