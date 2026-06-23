package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.client.BuganairGliderClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class LocalPlayerGliderMixin {

    private boolean buganair$wasGlidingLastTick = false;

    @Inject(method = "changeLookDirection(DD)V", at = @At("HEAD"), cancellable = true)
    private void buganair$overrideGliderLookInput(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if ((Object) this instanceof ClientPlayerEntity player) {
            if (BuganairGliderClientState.isGliding()) {
                // First-frame initialization to prevent reflection/snapping on item click
                if (!buganair$wasGlidingLastTick) {
                    BuganairGliderClientState.setFlightAngles(player.getPitch(), player.getYaw());
                    buganair$wasGlidingLastTick = true;
                }

                MinecraftClient client = MinecraftClient.getInstance();
                double sensitivity = client.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
                sensitivity = sensitivity * sensitivity * sensitivity * 8.0 * 0.15;

                // Accumulate clean unconstrained angles
                BuganairGliderClientState.accumulateLook(cursorDeltaX, cursorDeltaY, sensitivity);

                // Re-expose to player instance
                player.setYaw(BuganairGliderClientState.getYaw());
                player.setPitch(BuganairGliderClientState.getPitch());

                // TODO: Send your custom packet here to update the server:
                // ClientPlayNetworking.send(new BuganairGliderPacket(flightPitch, flightYaw));

                ci.cancel();
            } else {
                buganair$wasGlidingLastTick = false;
            }
        }
    }
}