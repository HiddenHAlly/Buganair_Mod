package net.hiddenhally.buganair;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.hiddenhally.buganair.client.BuganairBoatEntityRenderer;
import net.hiddenhally.buganair.client.BuganairBoatScreen; // Make sure this matches your package path!
import net.hiddenhally.buganair.client.BuganairSpruceBoatModel;
import net.hiddenhally.buganair.client.Buganair_Converted;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.hiddenhally.buganair.network.BuganairBoatInputPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Colors; // Assicurati di importare questo
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import net.hiddenhally.buganair.client.BuganairSniperClientState;
import net.hiddenhally.buganair.item.BuganairSniperItem;
import net.hiddenhally.buganair.network.BuganairSniperFirePayload;
import net.minecraft.util.Hand;

public class BuganairModClient implements ClientModInitializer {
    private static KeyBinding horizontalSpeedUpKey;
    private static KeyBinding horizontalSpeedDownKey;
    private static KeyBinding verticalSpeedUpKey;
    private static KeyBinding verticalSpeedDownKey;
    private static final KeyBinding.Category BOAT_CATEGORY = KeyBinding.Category.create(Identifier.of(Buganair.MOD_ID, "buganair"));

    public static final net.minecraft.client.render.entity.model.EntityModelLayer BUGANAIR_SPRUCE_BOAT_LAYER =
            new net.minecraft.client.render.entity.model.EntityModelLayer(
                    Identifier.of(Buganair.MOD_ID, "buganair_spruce_boat"), "main"
            );

    @Override
    public void onInitializeClient() {

        // Put this along with your other registrations:
        EntityModelLayerRegistry.registerModelLayer(
                Buganair_Converted.LAYER_LOCATION,
                Buganair_Converted::getTexturedModelData
        );

        // Register the custom spruce boat model parts layout
        net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry.registerModelLayer(
                BUGANAIR_SPRUCE_BOAT_LAYER,
                BuganairSpruceBoatModel::getTexturedModelData
        );

        EntityRendererFactories.register(BuganairMod.BUGANAIR_ACACIA_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_BAMBOO_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_BIRCH_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_CHERRY_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_DARK_OAK_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_JUNGLE_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_MANGROVE_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_OAK_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_PALE_OAK_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_SPRUCE_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);

        // ==========================================
        // ADD YOUR SCREEN REGISTRATION HERE
        // ==========================================
        HandledScreens.register(BuganairMod.BUGANAIR_BOAT_SCREEN_HANDLER, BuganairBoatScreen::new);

        horizontalSpeedUpKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.buganair.boat_speed_horizontal_up",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
            BOAT_CATEGORY
        ));

        horizontalSpeedDownKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.buganair.boat_speed_horizontal_down",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
            BOAT_CATEGORY
        ));

        verticalSpeedUpKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.buganair.boat_speed_vertical_up",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT,
            BOAT_CATEGORY
        ));

        verticalSpeedDownKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.buganair.boat_speed_vertical_down",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT,
            BOAT_CATEGORY
        ));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) {
                return;
            }

            if (client.player.getVehicle() instanceof BuganairBoatEntity boat) {
                while (horizontalSpeedUpKey.wasPressed()) {
                    boat.adjustHorizontalSpeed(1);
                }

                while (horizontalSpeedDownKey.wasPressed()) {
                    boat.adjustHorizontalSpeed(-1);
                }

                while (verticalSpeedUpKey.wasPressed()) {
                    boat.adjustVerticalSpeed(1);
                }

                while (verticalSpeedDownKey.wasPressed()) {
                    boat.adjustVerticalSpeed(-1);
                }

                int forward = 0;
                if (client.options.forwardKey.isPressed()) {
                    forward += 1;
                }
                if (client.options.backKey.isPressed()) {
                    forward -= 1;
                }

                int sideways = 0;
                if (client.options.rightKey.isPressed()) {
                    sideways += 1;
                }
                if (client.options.leftKey.isPressed()) {
                    sideways -= 1;
                }

                int vertical = 0;
                if (client.options.jumpKey.isPressed()) {
                    vertical += 1;
                }
                if (client.options.sprintKey.isPressed()) {
                    vertical -= 1;
                }

                boat.setMovementInput(forward, sideways, vertical);
                ClientPlayNetworking.send(new BuganairBoatInputPayload(
                    boat.getId(),
                    forward,
                    sideways,
                    vertical,
                    boat.getHorizontalSpeed(),
                    boat.getVerticalSpeed()
                ));
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null || client.currentScreen != null) {
                return;
            }

            var heldItem = client.player.getMainHandStack().getItem();
            boolean holdingSniper = heldItem instanceof BuganairSniperItem;

            if (!holdingSniper) {
                // Se cambi arma mentre sei in mira, esci automaticamente
                if (BuganairSniperClientState.isAiming()) {
                    BuganairSniperClientState.setAiming(false);
                }
                return;
            }

            // Tasto destro (piazzare blocco/usare oggetto) → attiva/disattiva la mira
            while (client.options.useKey.wasPressed()) {
                BuganairSniperClientState.toggleAiming();
            }

            // Tasto sinistro (attacco) → spara, solo se si è in mira
            if (BuganairSniperClientState.isAiming()) {
                while (client.options.attackKey.wasPressed()) {
                    long now = client.world.getTime();
                    if (BuganairSniperClientState.canFire(now, BuganairMod.SNIPER_FIRE_COOLDOWN_TICKS)) {
                        BuganairSniperClientState.markFired(now);
                        ClientPlayNetworking.send(new BuganairSniperFirePayload());
                        client.player.swingHand(Hand.MAIN_HAND);
                    }
                }
            }
            // Se NON si è in mira, attackKey non viene toccato: il pugno/attacco normale funziona.
        });

        HudElementRegistry.addLast(
                Identifier.of(Buganair.MOD_ID, "boat_speed_hud"),
                (drawContext, tickCounter) -> {
                    MinecraftClient client = MinecraftClient.getInstance();

                    if (client.player == null || client.options.hudHidden) {
                        return;
                    }

                    if (client.player.getVehicle() instanceof BuganairBoatEntity boat) {
                        Text speedText = Text.literal("Boat speed ")
                                .formatted(Formatting.WHITE)
                                .append(Text.literal("H " + boat.getHorizontalSpeed() + " b/s  ").formatted(Formatting.GREEN, Formatting.BOLD))
                                .append(Text.literal("V " + boat.getVerticalSpeed() + " b/s").formatted(Formatting.RED, Formatting.BOLD));

                        int x = drawContext.getScaledWindowWidth() / 2;
                        int y = drawContext.getScaledWindowHeight() - 65;

                        // Risolto: Colors.WHITE usa il formato ARGB corretto (-1 o 0xFFFFFFFF)
                        drawContext.drawCenteredTextWithShadow(client.textRenderer, speedText, x, y, Colors.WHITE);
                    }
                }
        );

        HudElementRegistry.addLast(
                Identifier.of(Buganair.MOD_ID, "sniper_scope_overlay"),
                (drawContext, tickCounter) -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null || client.options.hudHidden) return;
                    if (!BuganairSniperClientState.isAiming()) return;
                    if (!(client.player.getMainHandStack().getItem() instanceof BuganairSniperItem)) return;

                    int width = drawContext.getScaledWindowWidth();
                    int height = drawContext.getScaledWindowHeight();
                    int centerX = width / 2;
                    int centerY = height / 2;

                    // Dynamic radius matching the smaller viewport dimension
                    int radius = Math.min(width, height) / 2 - 20;
                    int blackColor = 0xFF000000;
                    int reticleColor = 0xFF20FF20; // Neon Green Tactical Line

                    // 1. Draw solid black mask outside the scope ring
                    if (centerY - radius > 0) drawContext.fill(0, 0, width, centerY - radius, blackColor);
                    if (centerY + radius < height) drawContext.fill(0, centerY + radius, width, height, blackColor);

                    for (int y = Math.max(0, centerY - radius); y < Math.min(height, centerY + radius); y++) {
                        double dx = Math.sqrt((double) radius * radius - (double) (y - centerY) * (y - centerY));
                        int holeLeft = (int) (centerX - dx);
                        int holeRight = (int) (centerX + dx);
                        if (holeLeft > 0) drawContext.fill(0, y, holeLeft, y + 1, blackColor);
                        if (holeRight < width) drawContext.fill(holeRight, y, width, y + 1, blackColor);
                    }

                    // 2. Fine crosshair line structure with an open central target dot area
                    int gap = 4;
                    int length = 40;

                    // Horizontal reticle bars
                    drawContext.fill(centerX - length, centerY, centerX - gap, centerY + 1, reticleColor);
                    drawContext.fill(centerX + gap, centerY, centerX + length, centerY + 1, reticleColor);

                    // Vertical reticle bars
                    drawContext.fill(centerX, centerY - length, centerX + 1, centerY - gap, reticleColor);
                    drawContext.fill(centerX, centerY + gap, centerX + 1, centerY + length, reticleColor);

                    // 3. Precision distance drop ticks (Mil-dots simulation)
                    for (int i = 1; i <= 4; i++) {
                        int step = i * 8;
                        // Vertical Drop markers
                        drawContext.fill(centerX - 2, centerY + gap + step, centerX + 3, centerY + gap + step + 1, reticleColor);
                        // Horizontal range windage indicators
                        drawContext.fill(centerX - gap - step - 1, centerY - 2, centerX - gap - step, centerY + 3, reticleColor);
                        drawContext.fill(centerX + gap + step, centerY - 2, centerX + gap + step + 1, centerY + 3, reticleColor);
                    }

                    // 4. Clean text overlay showing magnifying value
                    int zoom = BuganairSniperClientState.getZoomLevel();
                    Text zoomText = Text.literal("MAG: x" + zoom).formatted(Formatting.GREEN, Formatting.BOLD);
                    drawContext.drawCenteredTextWithShadow(client.textRenderer, zoomText, centerX, centerY + radius - 25, 0xFFFFFFFF);
                }
        );
    }
}
