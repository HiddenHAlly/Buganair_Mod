package net.hiddenhally.buganair.mixin.client;

import net.hiddenhally.buganair.client.BuganairGliderClientState;
import net.hiddenhally.buganair.item.BuganairHangGliderItem;
import net.hiddenhally.buganair.network.BuganairGliderTogglePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerGliderTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void buganair$checkGliderActivation(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        // Se indossa il deltaplano
        if (BuganairHangGliderItem.isWearingGlider(player)) {

            // DISATTIVAZIONE: Se tocca terra o è in acqua, smette di planare automaticamente
            if (BuganairGliderClientState.isGliding()) {
                if (player.isOnGround()) {
                    BuganairGliderClientState.setGliding(false);
                    ClientPlayNetworking.send(new BuganairGliderTogglePayload(false));
                }
            }
            // ATTIVAZIONE: Se è a mezz'aria, sta cadendo e preme il tasto salto (e non sta già planando)
            else {
                if (!player.isOnGround() && player.isGliding()) {
                    // Controlla se il giocatore ha appena premuto il tasto salto a mezz'aria
                    if (player.input.playerInput.jump()) {// && player.getVelocity().y < 0) {
                        BuganairGliderClientState.setGliding(true);
                        ClientPlayNetworking.send(new BuganairGliderTogglePayload(true));
                    }
                }
            }
        } else {
            // Se si toglie il deltaplano dall'inventario mentre plana, spegni lo stato
            if (BuganairGliderClientState.isGliding()) {
                BuganairGliderClientState.setGliding(false);
                ClientPlayNetworking.send(new BuganairGliderTogglePayload(false));
            }
        }
    }
}