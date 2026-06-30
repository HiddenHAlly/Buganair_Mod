package net.hiddenhally.buganair.client;

import net.hiddenhally.buganair.screen.AetherForgeScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.hiddenhally.buganair.Buganair.MOD_ID;

public class AetherForgeScreen extends HandledScreen<AetherForgeScreenHandler> {
    // Path to your custom GUI texture asset (176x166 px standard furnace grid)
    private static final Identifier TEXTURE = Identifier.of(MOD_ID, "textures/gui/container/aether_forge.png");

    public AetherForgeScreen(AetherForgeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        // Centers the title texts slightly based on standard layouts
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        // 1. Draw the main background container (Standard size 176x166 inside a 256x256 file)
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                x, y,
                0, 0,
                this.backgroundWidth, this.backgroundHeight,
                256, 256
        );

        // 2. Render Burn Flame (Fuel)
        if (this.handler.getFuelTime() > 0) {
            int fuelProgress = (this.handler.getFuelTime() * 13) / Math.max(1, this.handler.getMaxFuelTime());
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + 56, y + 36 + 12 - fuelProgress,
                    176, 12 - fuelProgress,
                    14, fuelProgress + 1,
                    256, 256
            );
        }

        // 3. Render Cook Arrow (Smelting)
        if (this.handler.getMaxProgress() > 0) {
            int cookProgress = (this.handler.getProgress() * 24) / this.handler.getMaxProgress();
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + 79, y + 34,
                    176, 14,
                    cookProgress + 1, 16,
                    256, 256
            );
        }

        // 4. Render Breeze Booster Indicator
        if (this.handler.getBreezeCharges() > 0) {
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    x + 141, y + 37,
                    176, 31,
                    18, 4,
                    256, 256
            );
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}