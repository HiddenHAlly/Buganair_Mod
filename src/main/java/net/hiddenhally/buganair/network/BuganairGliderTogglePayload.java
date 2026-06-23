package net.hiddenhally.buganair.network;

import net.hiddenhally.buganair.Buganair;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BuganairGliderTogglePayload(boolean isGliding) implements CustomPayload {
    public static final CustomPayload.Id<BuganairGliderTogglePayload> ID = new CustomPayload.Id<>(Identifier.of(Buganair.MOD_ID, "glider_toggle"));

    public static final PacketCodec<RegistryByteBuf, BuganairGliderTogglePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, BuganairGliderTogglePayload::isGliding,
            BuganairGliderTogglePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}