package net.hiddenhally.buganair;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.hiddenhally.buganair.client.*;
import net.hiddenhally.buganair.config.BuganairConfig;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.hiddenhally.buganair.network.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import org.lwjgl.glfw.GLFW;
import net.hiddenhally.buganair.item.BuganairSniperItem;
import net.hiddenhally.buganair.item.BuganairHangGliderItem;

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
        // Add this near the top
        BuganairRadarClientState.registerRenderer();
        BuganairScoutingFlareClientState.registerRenderer();

        ClientPlayNetworking.registerGlobalReceiver(BuganairRadarSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                BuganairRadarClientState.startRadar(payload.center());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(BuganairScoutingFlareSyncPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                BuganairScoutingFlareClientState.startRadar(payload.center());
            });
        });

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

        EntityRendererFactories.register(
                BuganairMod.SCOUTING_FLARE_ENTITY_TYPE,
                net.minecraft.client.render.entity.FlyingItemEntityRenderer::new
        );
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
                    // Sync to server that we are no longer aiming because we swapped items
                    ClientPlayNetworking.send(new BuganairSniperScopePayload(false));
                }
                return;
            }

            // Tasto destro (piazzare blocco/usare oggetto) → attiva/disattiva la mira
            while (client.options.useKey.wasPressed()) {
                BuganairSniperClientState.toggleAiming();

                // THE FIX: Send the new state to the server to update the scoreboard
                ClientPlayNetworking.send(new BuganairSniperScopePayload(BuganairSniperClientState.isAiming()));
            }

            // Tasto sinistro (attacco) → spara, solo se si è in mira
            if (BuganairSniperClientState.isAiming()) {
                while (client.options.attackKey.wasPressed()) {
                    long now = client.world.getTime();
                    if (BuganairSniperClientState.canFire(now, BuganairConfig.INSTANCE.SNIPER_FIRE_COOLDOWN_TICKS)) {
                        BuganairSniperClientState.markFired(now);
                        ClientPlayNetworking.send(new BuganairSniperFirePayload());
                        client.player.swingHand(Hand.MAIN_HAND);
                        // Inside your firing method:
                        client.player.getItemCooldownManager().set(BuganairMod.BUGANAIR_SNIPER_ITEM.getDefaultStack(), BuganairConfig.INSTANCE.SNIPER_FIRE_COOLDOWN_TICKS); // 20 ticks = 1 second cooldown
                    }
                }
            }
            // Se NON si è in mira, attackKey non viene toccato: il pugno/attacco normale funziona.
        });

//        ClientTickEvents.START_CLIENT_TICK.register(client -> {
//            if (client.player == null || client.world == null || client.currentScreen != null) {
//                return;
//            }
//
//            boolean hasGliderInMainHand = client.player.getMainHandStack().getItem() instanceof BuganairHangGliderItem;
//            boolean hasGliderInOffHand = client.player.getOffHandStack().getItem() instanceof BuganairHangGliderItem;
//            boolean holdingGlider = hasGliderInMainHand || hasGliderInOffHand;
//
//            if (holdingGlider) {
//                // Toggle the glider state when the use key (right-click) is pressed
//                while (client.options.useKey.wasPressed()) {
//                    // Prevent toggling if the player is safely on the ground
//                    if (!client.player.isOnGround()) {
//                        BuganairGliderClientState.toggleGliding();
//                        ClientPlayNetworking.send(new BuganairGliderTogglePayload(BuganairGliderClientState.isGliding()));
//                    }
//                }
//            } else {
//                // Auto-disable if the player switches off the item while gliding
//                if (BuganairGliderClientState.isGliding()) {
//                    BuganairGliderClientState.setGliding(false);
//                    ClientPlayNetworking.send(new BuganairGliderTogglePayload(false));
//                }
//            }
//        });



        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean holdingGlider = client.player.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof BuganairHangGliderItem;

            if (BuganairGliderClientState.isGliding() && (client.player.isOnGround() || !holdingGlider)) {
                BuganairGliderClientState.setGliding(false);
                ClientPlayNetworking.send(new BuganairGliderTogglePayload(false));
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (BuganairGliderClientState.isGliding()) {
                // Ground landing auto-cancel check
                if (client.player.isOnGround()) {
                    BuganairGliderClientState.setGliding(false);
                    ClientPlayNetworking.send(new BuganairGliderOrientationPayload(0, 0, 0));
                    // THE FIX: Send the new state to the server to update the scoreboard
                    ClientPlayNetworking.send(new BuganairGliderPayload(false));
                    return;
                }
                // THE FIX: Send the new state to the server to update the scoreboard
                ClientPlayNetworking.send(new BuganairGliderPayload(true));

                // Gather keyboard strafe keys for A/D yaw maneuvers
                boolean strafeLeft  = client.options.leftKey.isPressed();
                boolean strafeRight = client.options.rightKey.isPressed();
                // 1. Calcola la fisica dell'input della tastiera
                BuganairGliderClientState.handleKeyboardInput(strafeLeft, strafeRight);

                // 2. LA CHIAVE: Applica subito i nuovi angoli al giocatore per evitare lo scatto visivo!
                client.player.setYaw(BuganairGliderClientState.getYaw());
                client.player.setPitch(BuganairGliderClientState.getPitch());

                // 3. Invia i dati aggiornati al server per sincronizzare la fisica di volo sul server

                // Send current orientation payload to server for validation
                ClientPlayNetworking.send(new BuganairGliderOrientationPayload(
                        BuganairGliderClientState.getPitch(),
                        BuganairGliderClientState.getYaw(),
                        BuganairGliderClientState.getRoll()
                ));
            } else {
                ClientPlayNetworking.send(new BuganairGliderPayload(false));
            }
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
                    if (client.player == null) return;
                    if (!BuganairSniperClientState.isAiming()) return;
                    if (!(client.player.getMainHandStack().getItem() instanceof BuganairSniperItem)) return;

                    int width = drawContext.getScaledWindowWidth();
                    int height = drawContext.getScaledWindowHeight();
                    int centerX = width / 2;
                    int centerY = height / 2;
                    int radius = Math.min(width, height) / 2 - 20;

                    int blackColor = 0xAA000000;

                    // 1. PERFECT CIRCULAR CUTOUT (Math instead of Scissor)
                    // Fill the solid black areas above and below the circular lens
                    if (centerY - radius > 0) drawContext.fill(0, 0, width, centerY - radius, blackColor);
                    if (centerY + radius < height) drawContext.fill(0, centerY + radius, width, height, blackColor);

                    // Carve out the circular hole row by row
                    for (int y = Math.max(0, centerY - radius); y <= Math.min(height, centerY + radius); y++) {
                        double dy = y - centerY;
                        double dx = Math.sqrt((radius * radius) - (dy * dy));
                        int holeLeft = (int) (centerX - dx);
                        int holeRight = (int) (centerX + dx);

                        // Draw left side of the screen up to the circle's edge
                        if (holeLeft > 0) drawContext.fill(0, y, holeLeft, y + 1, blackColor);
                        // Draw right side of the screen from the circle's edge to the border
                        if (holeRight < width) drawContext.fill(holeRight, y, width, y + 1, blackColor);
                    }

                    // 2. VISUAL COOLDOWN BAR
                    float cooldown = client.player.getItemCooldownManager().getCooldownProgress(client.player.getMainHandStack(), 0.0f);
                    if (cooldown > 0) {
                        int barWidth = 60;
                        int barHeight = 4;
                        int x1 = centerX - (barWidth / 2);
                        int y1 = centerY + 30;
                        drawContext.fill(x1, y1, x1 + barWidth, y1 + barHeight, 0x88000000); // BG
                        drawContext.fill(x1, y1, x1 + (int)(barWidth * (1 - cooldown)), y1 + barHeight, 0xFFFF0000); // Progress (Red)
                    }

                    // 3. RETICLE
                    int color = 0xFF20FF20;
                    drawContext.fill(centerX - 20, centerY, centerX - 5, centerY + 1, color);
                    drawContext.fill(centerX + 5, centerY, centerX + 20, centerY + 1, color);
                    drawContext.fill(centerX, centerY - 20, centerX + 1, centerY - 5, color);
                    drawContext.fill(centerX, centerY + 5, centerX + 1, centerY + 20, color);

                    // 4. MAGNIFICATION TEXT
                    int zoom = BuganairSniperClientState.getZoomLevel();
                    net.minecraft.text.Text zoomText = net.minecraft.text.Text.literal("MAG: x" + zoom).formatted(net.minecraft.util.Formatting.GREEN, net.minecraft.util.Formatting.BOLD);
                    drawContext.drawCenteredTextWithShadow(client.textRenderer, zoomText, centerX, centerY + radius - 25, 0xFFFFFFFF);
                }
        );
    }
}
