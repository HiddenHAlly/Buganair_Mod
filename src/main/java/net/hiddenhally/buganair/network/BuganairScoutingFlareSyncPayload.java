package net.hiddenhally.buganair.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static net.hiddenhally.buganair.Buganair.MOD_ID;

public record BuganairScoutingFlareSyncPayload(BlockPos center) implements CustomPayload {
    public static final CustomPayload.Id<BuganairScoutingFlareSyncPayload> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "scouting_flare_sync"));

    public static final PacketCodec<RegistryByteBuf, BuganairScoutingFlareSyncPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, BuganairScoutingFlareSyncPayload::center,
            BuganairScoutingFlareSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}