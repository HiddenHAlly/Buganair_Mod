package net.hiddenhally.buganair.mixin; // Matches your package layout

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Redirect(
            method = "drawForeground",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V"
            )
    )
    private void injectTextShadow(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
        HandledScreen<?> screen = (HandledScreen<?>)(Object)this;

        // 1. Check if the current screen has a main title
        if (screen.getTitle() != null) {
            String titleString = screen.getTitle().getString();

            // 2. Identify your boat by checking its custom title name
            if (titleString.toLowerCase().contains("buganair boat")) {

                // 3. Apply custom style to the main screen title text
                if (text.equals(screen.getTitle())) {
                    // Custom Color Example: 0xFFFFAA00 (Gold) or keep 'color' for default vanilla black
                    int customTitleColor = 0xFFFFAA00;
                    context.drawText(textRenderer, text, x, y, customTitleColor, true); // Flip shadow to true
                    return;
                }

                // 4. NEW: Apply custom style to the bottom "Inventory" title text
                // We use accessor/field matching if playerInventoryTitle is exposed, or check against the fallback Text object
                // In vanilla HandledScreen, this draws screen.playerInventoryTitle
                // Let's safe-check if it matches the text string or matches the screen's sub-component via a safe string comparison
                if (text.getString().equals(net.minecraft.text.Text.translatable("container.inventory").getString()) || text.getString().equalsIgnoreCase("inventory")) {
                    // Custom Color Example: 0xFFFF5555 (Light Red) or 0xFFFFFFFF (White)
                    int customInventoryColor = 0xFFFFFFFF;
                    context.drawText(textRenderer, text, x, y, customInventoryColor, true); // Flip shadow to true
                    return;
                }
            }
        }

        // Fallback to default vanilla rendering for everything else
        context.drawText(textRenderer, text, x, y, color, shadow);
    }
}