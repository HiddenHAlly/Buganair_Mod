package net.hiddenhally.buganair.client;

import net.hiddenhally.buganair.Buganair;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

// Changed superclass from GenericContainerScreen to HandledScreen
public class BuganairBoatScreen extends HandledScreen<GenericContainerScreenHandler> {

    // Path to your custom brown 9x6 inventory texture image
    private static final Identifier TEXTURE = Identifier.of(Buganair.MOD_ID, "textures/gui/container/buganair_generic_9x6.png");

    public BuganairBoatScreen(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        // Automatically adjusts screen height variables to fit a 9x6 container layout
        this.backgroundWidth = 176;
        this.backgroundHeight = 114 + 6 * 18; // 222
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        // Clean 1.21.1 DrawContext call
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x,
                y,
                0,
                0,
                this.backgroundWidth,
                this.backgroundHeight,
                256,
                256
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}