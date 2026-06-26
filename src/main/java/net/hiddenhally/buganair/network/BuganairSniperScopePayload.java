package net.hiddenhally.buganair.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BuganairSniperScopePayload(boolean isAiming) implements CustomPayload {
    public static final Id<BuganairSniperScopePayload> ID = new Id<>(Identifier.of("buganair", "sniper_scope"));
    public static final PacketCodec<RegistryByteBuf, BuganairSniperScopePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, BuganairSniperScopePayload::isAiming,
            BuganairSniperScopePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}