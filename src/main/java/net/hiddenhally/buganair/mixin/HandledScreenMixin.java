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
                    // Changed the ending from 'Z)I' to 'Z)V' because drawText returns void
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V"
            )
    )
    // Changed return type from 'int' to 'void'
    private void injectTextShadow(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow) {
        HandledScreen<?> screen = (HandledScreen<?>)(Object)this;

        // 1. Check if the current screen has a title
        if (screen.getTitle() != null) {
            String titleString = screen.getTitle().getString();

            // 2. Identify your boat by checking its custom title name
            if (titleString.toLowerCase().contains("buganair boat")) {

                // 3. Apply the shadow ONLY to the main title text
                if (text.equals(screen.getTitle())) {
                    context.drawText(textRenderer, text, x, y, color, true); // Flip shadow to true
                    return; // Exit early since we drew the text
                }
            }
        }

        // Fallback to default vanilla rendering for everything else
        context.drawText(textRenderer, text, x, y, color, shadow);
    }
}