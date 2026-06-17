package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.client.BuganairSniperClientState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class BuganairFovMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void buganair$applyScopeZoom(Camera camera, float tickDelta, boolean changingFov,
                                         CallbackInfoReturnable<Float> cir) {
        float divisor = BuganairSniperClientState.getZoomDivisor();
        if (divisor > 1.0f) {
            cir.setReturnValue(cir.getReturnValue() / divisor);
        }
    }
}