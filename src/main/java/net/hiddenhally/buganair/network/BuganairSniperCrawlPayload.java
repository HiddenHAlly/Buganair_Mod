package net.hiddenhally.buganair.network;

import net.hiddenhally.buganair.Buganair;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Inviato dal client al server quando il giocatore entra o esce dal crawl con lo sniper. */
public record BuganairSniperCrawlPayload(boolean crawling) implements CustomPayload {

    public static final Id<BuganairSniperCrawlPayload> ID =
            new Id<>(Identifier.of(Buganair.MOD_ID, "sniper_crawl"));

    public static final PacketCodec<RegistryByteBuf, BuganairSniperCrawlPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.BOOLEAN, BuganairSniperCrawlPayload::crawling,
                    BuganairSniperCrawlPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}