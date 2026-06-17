package net.hiddenhally.buganair.network;

import net.hiddenhally.buganair.Buganair;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

// Payload "vuoto": il server calcola direzione e posizione dalla rotazione
// sincronizzata del giocatore, niente da inviare dal client.
public record BuganairSniperFirePayload() implements CustomPayload {
    public static final CustomPayload.Id<BuganairSniperFirePayload> ID =
            new CustomPayload.Id<>(Identifier.of(Buganair.MOD_ID, "sniper_fire"));

    public static final PacketCodec<RegistryByteBuf, BuganairSniperFirePayload> CODEC =
            PacketCodec.unit(new BuganairSniperFirePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}