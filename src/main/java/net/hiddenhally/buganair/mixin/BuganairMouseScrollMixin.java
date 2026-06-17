package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.client.BuganairSniperClientState;
import net.hiddenhally.buganair.item.BuganairSniperItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class BuganairMouseScrollMixin {

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void buganair$onScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !BuganairSniperClientState.isAiming()) {
            return;
        }
        if (!(client.player.getMainHandStack().getItem() instanceof BuganairSniperItem)) {
            return;
        }

        if (vertical > 0) {
            BuganairSniperClientState.adjustZoom(1);
        } else if (vertical < 0) {
            BuganairSniperClientState.adjustZoom(-1);
        }
        ci.cancel(); // evita che la rotellina cambi anche lo slot della hotbar
    }
}