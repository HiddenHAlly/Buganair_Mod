package net.hiddenhally.buganair.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;

public class BuganairBoatScreen extends GenericContainerScreen {
    public BuganairBoatScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // 4210752 is vanilla's dark gray color (0x404040)
        // The last argument 'true' forces the text shadow on!
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, true);

        // Keeps the player inventory title normal (or make it true if you want both shadowed)
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
    }
}