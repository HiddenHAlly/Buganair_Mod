package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.client.BuganairSniperClientState;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public class BuganairMouseSensitivityMixin {

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void buganair$scaleSniperSensitivity(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY) {
        if (BuganairSniperClientState.isAiming()) {
            // Divide the mouse movement by the zoom level.
            // Zoom level 4 = 4x slower sensitivity.
            float divisor = BuganairSniperClientState.getZoomDivisor();
            player.changeLookDirection(cursorDeltaX / divisor, cursorDeltaY / divisor);
        } else {
            // Normal vanilla camera movement
            player.changeLookDirection(cursorDeltaX, cursorDeltaY);
        }
    }
}