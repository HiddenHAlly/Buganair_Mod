package net.hiddenhally.buganair.client;

import net.hiddenhally.buganair.BuganairMod;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class BuganairRecipeScreen extends Screen {

    // Add your texture identifier at the top of the class
    private static final Identifier BACKGROUND_TEXTURE =
            Identifier.of("buganair", "textures/gui/buganair_bluemap.png");

    //private static final int PW = 240, PH = 220;

    // ── layout ────────────────────────────────────────────────────────────────
    private static final int PW = 1281/2, PH = 832/2; // dimensioni pannello
    private static final int SLOT = 18, GAP = 2, CELL = SLOT + GAP; // 20px/cella
    private static final int OFFSET_X = 100, OFFSET_Y = 35;

    // Posizioni relative al pannello (px, py)
    private static final int GRID_X = 30+OFFSET_X, GRID_Y = 52+OFFSET_Y;
    private static final int ARROW_X = GRID_X + 3 * CELL + 18; // 108
    private static final int ARROW_Y = GRID_Y + CELL + 5;       //  77
    private static final int RES_X   = GRID_X + 3 * CELL + 32; // 122
    private static final int RES_Y   = GRID_Y + CELL;           //  72

    // ── colori (ARGB) ─────────────────────────────────────────────────────────
    private static final int C_BORDER    = 0xFF3E2000;
    private static final int C_PARCHMENT = 0xFFD4A96A;
    private static final int C_INNER     = 0xFFEDD59A;
    private static final int C_SLOT_DK   = 0xFF8B6040;
    private static final int C_SLOT_LT   = 0xFFCCA870;
    private static final int C_SEP       = 0xFF6B4A28;
    private static final int C_GOLD_RIM  = 0xFFB8A020;
    private static final int C_TXT_TITLE = 0xFFFFF1E5;//322215;//B4794B;//CFA98C;//EBCBB2;//4E3927;//8B6546;//0xFFAB7D57;//0xFF3E1A00;
    private static final int C_TXT_BODY  = 0xFFFFF3E8;//0xFF5C3010;
    private static final int C_TXT_HINT  = 0xFFC79F73;//0xFF8B6040;

    // ── recipe: dsd / cpc / ddd ───────────────────────────────────────────────
    private static final ItemStack[] RECIPE = {
            new ItemStack(Items.DIAMOND),
            new ItemStack(Items.WOODEN_SHOVEL),
            new ItemStack(Items.DIAMOND),
            new ItemStack(Items.CHEST),
            new ItemStack(Items.OAK_BOAT),
            new ItemStack(Items.CHEST),
            new ItemStack(Items.DIAMOND),
            new ItemStack(BuganairMod.BUGANAIR_RECIPE_MAP_ITEM),
            new ItemStack(Items.DIAMOND),
    };

    private ItemStack resultStack;
    private ItemStack hoveredStack = ItemStack.EMPTY;

    public BuganairRecipeScreen() {
        super(Text.translatable("screen.buganair.recipe_map"));
    }

    @Override
    protected void init() {
        super.init();
        resultStack = new ItemStack(BuganairMod.BUGANAIR_OAK_BOAT_ITEM);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    // ── render principale ─────────────────────────────────────────────────────

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        // In 1.21, draw the background first before custom rendering
        // ❌ LINE 73: This causes a double-blur crash in 1.21.2+
        //this.renderBackground(context, mouseX, mouseY, delta);

        super.render(ctx, mx, my, delta);

        int px = (this.width  - PW) / 2;
        int py = (this.height - PH) / 2;

        drawPanel(ctx, px, py);
        drawTitle(ctx, px, py);
        drawSeparator(ctx, px, py);
        drawSubtitle(ctx, px, py);

        // Resetta l'hover e lo ridetermina durante i draw
        hoveredStack = ItemStack.EMPTY;
        drawGrid(ctx, px, py, mx, my);
        drawArrowAndResult(ctx, px, py, mx, my);

        //drawIngredientsList(ctx, px, py);
        drawCloseHint(ctx, px, py);

        // Render tooltips after drawing items
        if (!hoveredStack.isEmpty()) {
            ctx.drawItemTooltip(this.textRenderer, hoveredStack, mx, my);
        }

        super.render(ctx, mx, my, delta);
    }

    // ── helper di disegno ─────────────────────────────────────────────────────

    // Replace your drawPanel method with this:
    private void drawPanel(DrawContext ctx, int px, int py) {
        // Draws your custom crusty image exactly where the panel goes
        ctx.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND_TEXTURE,
                px, py,           // Screen X, Y destination
                0, 0,             // Texture U, V source coordinates
                PW, PH,           // Width and Height to draw
                1281/2, 832/2          // Total width/height of your source PNG file
        );
    }

    private void drawTitle(DrawContext ctx, int px, int py) {
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("screen.buganair.recipe_map.title"),
                px + PW / 2, py + 13+OFFSET_Y, C_TXT_TITLE);
    }

    private void drawSeparator(DrawContext ctx, int px, int py) {
        ctx.fill(px + 14+OFFSET_X, py + 27+OFFSET_Y, px + PW - 14-OFFSET_X, py + 28+OFFSET_Y, C_SEP);
    }

    private void drawSubtitle(DrawContext ctx, int px, int py) {
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("screen.buganair.recipe_map.subtitle"),
                px + PW / 2, py + 34+OFFSET_Y, C_TXT_BODY);
    }

    private void drawGrid(DrawContext ctx, int px, int py, int mx, int my) {
        for (int i = 0; i < 9; i++) {
            int col = i % 3, row = i / 3;
            int sx = px + GRID_X + col * CELL;
            int sy = py + GRID_Y + row * CELL;

            // Sfondo slot
            ctx.fill(sx - 1, sy - 1, sx + SLOT + 1, sy + SLOT + 1, C_SLOT_DK);
            ctx.fill(sx,     sy,     sx + SLOT,     sy + SLOT,     C_SLOT_LT);

            ItemStack stack = RECIPE[i];
            if (!stack.isEmpty()) {
                ctx.drawItem(stack, sx + 1, sy + 1);
                ctx.drawStackOverlay(this.textRenderer, stack, sx + 1, sy + 1);
                if (mx >= sx && mx < sx + SLOT && my >= sy && my < sy + SLOT) {
                    hoveredStack = stack;
                }
            }
        }
    }

    private void drawArrowAndResult(DrawContext ctx, int px, int py, int mx, int my) {
        // Freccia
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("➜"),
                px + ARROW_X, py + ARROW_Y, C_TXT_TITLE);

        // Slot risultato con bordo dorato
        int rx = px + RES_X, ry = py + RES_Y;
        ctx.fill(rx - 3, ry - 3, rx + SLOT + 3, ry + SLOT + 3, C_GOLD_RIM);
        ctx.fill(rx - 1, ry - 1, rx + SLOT + 1, ry + SLOT + 1, C_SLOT_DK);
        ctx.fill(rx,     ry,     rx + SLOT,     ry + SLOT,     C_SLOT_LT);

        if (resultStack != null && !resultStack.isEmpty()) {
            ctx.drawItem(resultStack, rx + 1, ry + 1);
            ctx.drawStackOverlay(this.textRenderer, resultStack, rx + 1, ry + 1);
            if (mx >= rx && mx < rx + SLOT && my >= ry && my < ry + SLOT) {
                hoveredStack = resultStack;
            }
        }
    }

    private void drawIngredientsList(DrawContext ctx, int px, int py) {
        int lx = px + 18+OFFSET_X;
        int ly = py + GRID_Y + 3 * CELL + 12+OFFSET_Y;

        ctx.drawText(this.textRenderer,
                Text.translatable("screen.buganair.recipe_map.ingredients"),
                lx, ly, C_TXT_TITLE, false);

        String[] lines = { "· 4× Diamond", "· 2× Chest", "· 1× Oak Boat", "· 1× Wooden Shovel","· 1× Buganair Blueprint" };
        for (int i = 0; i < lines.length; i++) {
            ctx.drawText(this.textRenderer, Text.literal(lines[i]),
                    lx + 4, ly + 12 + i * 10, C_TXT_BODY, false);
        }
    }

    private void drawCloseHint(DrawContext ctx, int px, int py) {
        ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.translatable("screen.buganair.recipe_map.close_hint"),
                px + PW / 2, py + PH - 13, C_TXT_HINT);
    }

    // ── input ─────────────────────────────────────────────────────────────────

    @Override
    public boolean keyPressed(KeyInput input) {
        // Use input.key() to get the old keyCode
        if (input.key() == GLFW.GLFW_KEY_E) {
            this.close();
            return true;
        }
        // Pass the single KeyInput object to the super call
        return super.keyPressed(input);
    }
}