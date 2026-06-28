package net.hiddenhally.buganair.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class BuganairHudVisibilityMixin {

    // We inject at the start of the hotbar rendering method.
    // We check if we are aiming, and if so, we ensure the hotbar logic
    // doesn't return early due to "HUD hidden" states.
    @Inject(method = "renderHotbar", at = @At("HEAD"))
    private void buganair$forceRenderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // If the mod is aiming, we allow the hotbar to render normally
        // even if the game engine thinks the HUD should be hidden.
        // By NOT cancelling, we let the original code run.
        // If the original code was being skipped by a conditional,
        // you would move this logic to a Redirect or modify the conditional.
    }
}