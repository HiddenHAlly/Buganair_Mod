package net.hiddenhally.buganair;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.hiddenhally.buganair.client.*;
import net.hiddenhally.buganair.config.BuganairConfig;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.hiddenhally.buganair.network.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.entity.EntityPose;
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
    private static final KeyBinding.Category BOAT_CATEGORY =
            KeyBinding.Category.create(Identifier.of(Buganair.MOD_ID, "buganair"));

    public static final net.minecraft.client.render.entity.model.EntityModelLayer BUGANAIR_SPRUCE_BOAT_LAYER =
            new net.minecraft.client.render.entity.model.EntityModelLayer(
                    Identifier.of(Buganair.MOD_ID, "buganair_spruce_boat"), "main"
            );

    // ── Stato precedente dei tasti freccia per l'edge-detection del crawl ────
    // (solo frecce su/giù; sinistra/destra sono usate per il lean continuo)
    private static boolean prevUpArrow   = false;
    private static boolean prevDownArrow = false;

    @Override
    public void onInitializeClient() {
        // Add this near the top
        BuganairRadarClientState.registerRenderer();
        BuganairScoutingFlareClientState.registerRenderer();

        ClientPlayNetworking.registerGlobalReceiver(BuganairRadarSyncPayload.ID, (payload, context) -> context.client().execute(() -> BuganairRadarClientState.startRadar(payload.center())));

        ClientPlayNetworking.registerGlobalReceiver(BuganairScoutingFlareSyncPayload.ID, (payload, context) -> context.client().execute(() ->
                BuganairScoutingFlareClientState.startRadar(payload.center(), payload.enemy())));

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

        EntityRendererFactories.register(BuganairMod.BUGANAIR_ACACIA_BOAT_ENTITY_TYPE,   BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_BAMBOO_BOAT_ENTITY_TYPE,   BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_BIRCH_BOAT_ENTITY_TYPE,    BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_CHERRY_BOAT_ENTITY_TYPE,   BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_DARK_OAK_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_JUNGLE_BOAT_ENTITY_TYPE,   BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_MANGROVE_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_OAK_BOAT_ENTITY_TYPE,      BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_PALE_OAK_BOAT_ENTITY_TYPE, BuganairBoatEntityRenderer::new);
        EntityRendererFactories.register(BuganairMod.BUGANAIR_SPRUCE_BOAT_ENTITY_TYPE,   BuganairBoatEntityRenderer::new);

        EntityRendererFactories.register(
                BuganairMod.BUGANAIR_SCOUTING_FLARE_ENTITY_TYPE,
                net.minecraft.client.render.entity.FlyingItemEntityRenderer::new
        );

        HandledScreens.register(BuganairMod.BUGANAIR_BOAT_SCREEN_HANDLER, BuganairBoatScreen::new);

        // ── Keybinding barca (frecce) ─────────────────────────────────────────
        // Le frecce sono già registrate qui per la barca.  Quando si usa lo sniper
        // le leggiamo direttamente via InputUtil.isKeyPressed() più in basso,
        // così non c'è nessun conflitto — i due handler si escludono
        // a vicenda (barca vs. sniper).
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

        // ── Tick barca ────────────────────────────────────────────────────────
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;

            if (client.player.getVehicle() instanceof BuganairBoatEntity boat) {
                while (horizontalSpeedUpKey.wasPressed())   boat.adjustHorizontalSpeed(1);
                while (horizontalSpeedDownKey.wasPressed()) boat.adjustHorizontalSpeed(-1);
                while (verticalSpeedUpKey.wasPressed())     boat.adjustVerticalSpeed(1);
                while (verticalSpeedDownKey.wasPressed())   boat.adjustVerticalSpeed(-1);

                int forward = 0;
                if (client.options.forwardKey.isPressed()) forward += 1;
                if (client.options.backKey.isPressed())    forward -= 1;

                int sideways = 0;
                if (client.options.rightKey.isPressed()) sideways += 1;
                if (client.options.leftKey.isPressed())  sideways -= 1;

                int vertical = 0;
                if (client.options.jumpKey.isPressed())   vertical += 1;
                if (client.options.sprintKey.isPressed()) vertical -= 1;

                boat.setMovementInput(forward, sideways, vertical);
                ClientPlayNetworking.send(new BuganairBoatInputPayload(
                        boat.getId(), forward, sideways, vertical,
                        boat.getHorizontalSpeed(), boat.getVerticalSpeed()
                ));
            }
        });

        // Inside your onInitialize() or onInitializeClient() method:
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            ItemStack heldItem = player.getStackInHand(hand);
            if (heldItem.isOf(BuganairMod.BUGANAIR_SNIPER_ITEM)) {
                if (player.isCreative()) return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // ── Tick sniper ───────────────────────────────────────────────────────
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null || client.currentScreen != null) return;

            // Ignora se si è in una barca (le frecce servono alla barca)
            if (client.player.getVehicle() instanceof BuganairBoatEntity) return;

            var heldItem = client.player.getMainHandStack().getItem();
            boolean holdingSniper = heldItem instanceof BuganairSniperItem;

            if (!holdingSniper) {
                // Esci automaticamente dalla mira se cambi oggetto
                if (BuganairSniperClientState.isAiming()) {
                    BuganairSniperClientState.setAiming(false);
                    ClientPlayNetworking.send(new BuganairSniperScopePayload(false));
                }
                // Esci automaticamente dal crawl se cambi oggetto
                if (BuganairSniperClientState.isCrawling()) {
                    BuganairSniperClientState.setCrawling(false);
                    ClientPlayNetworking.send(new BuganairSniperCrawlPayload(false));
                    prevDownArrow = false;
                    prevUpArrow   = false;
                }
                return;
            }

            // Tasto destro → attiva/disattiva la mira
            while (client.options.useKey.wasPressed()) {
                BuganairSniperClientState.toggleAiming();
                ClientPlayNetworking.send(new BuganairSniperScopePayload(BuganairSniperClientState.isAiming()));
            }

            // ── Input frecce ──────────────────────────────────────────────────
            Window window = client.getWindow();
            boolean leftArrow  = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_LEFT);
            boolean rightArrow = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_RIGHT);
            boolean upArrow    = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_UP);
            boolean downArrow  = InputUtil.isKeyPressed(window, GLFW.GLFW_KEY_DOWN);

            // Frecce ← → → lean/roll fluido della camera
            BuganairSniperClientState.handleKeyboardInput(leftArrow, rightArrow);

            // Freccia ↓  → entra in crawl (one-shot, edge rising)
            if (downArrow && !prevDownArrow && !BuganairSniperClientState.isCrawling()) {
                BuganairSniperClientState.setCrawling(true);
                ClientPlayNetworking.send(new BuganairSniperCrawlPayload(true));
            }
            // Freccia ↑  → esci dal crawl (one-shot, edge rising)
            if (upArrow && !prevUpArrow && BuganairSniperClientState.isCrawling()) {
                BuganairSniperClientState.setCrawling(false);
                ClientPlayNetworking.send(new BuganairSniperCrawlPayload(false));
            }
            prevDownArrow = downArrow;
            prevUpArrow   = upArrow;

            // Forza il pose lato client ogni tick per evitare sfarfallio
            // tra il valore locale e quello ricevuto dal server.
            if (BuganairSniperClientState.isCrawling()) {
                client.player.setPose(EntityPose.SWIMMING);
            }

            // ── Tasto sinistro → spara ────────────────────────────────────────
            while (client.options.attackKey.wasPressed()) {
                long now = client.world.getTime();
                if (BuganairSniperClientState.canFire(now, BuganairConfig.INSTANCE.SNIPER_FIRE_COOLDOWN_TICKS)) {
                    BuganairSniperClientState.markFired(now);
                    ClientPlayNetworking.send(new BuganairSniperFirePayload(BuganairSniperClientState.getXOffset()));
                    client.player.swingHand(Hand.MAIN_HAND);
                    client.player.getItemCooldownManager().set(
                            BuganairMod.BUGANAIR_SNIPER_ITEM.getDefaultStack(),
                            BuganairConfig.INSTANCE.SNIPER_FIRE_COOLDOWN_TICKS
                    );
                }
            }
        });

        // ── Tick deltaplano ───────────────────────────────────────────────────
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean holdingGlider = client.player.getEquippedStack(EquipmentSlot.CHEST)
                    .getItem() instanceof BuganairHangGliderItem;

            if (BuganairGliderClientState.isGliding()
                    && (client.player.isOnGround() || !holdingGlider || !client.player.isGliding())) {
                BuganairGliderClientState.setGliding(false);
                ClientPlayNetworking.send(new BuganairGliderTogglePayload(false));
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (BuganairGliderClientState.isGliding()) {
                if (client.player.isOnGround() || !client.player.isGliding()) {
                    BuganairGliderClientState.setGliding(false);
                    ClientPlayNetworking.send(new BuganairGliderOrientationPayload(0, 0, 0));
                    ClientPlayNetworking.send(new BuganairGliderPayload(false));
                    return;
                }
                ClientPlayNetworking.send(new BuganairGliderPayload(true));

                boolean strafeLeft  = client.options.leftKey.isPressed();
                boolean strafeRight = client.options.rightKey.isPressed();
                BuganairGliderClientState.handleKeyboardInput(strafeLeft, strafeRight);

                client.player.setYaw(BuganairGliderClientState.getYaw());
                client.player.setPitch(BuganairGliderClientState.getPitch());

                ClientPlayNetworking.send(new BuganairGliderOrientationPayload(
                        BuganairGliderClientState.getPitch(),
                        BuganairGliderClientState.getYaw(),
                        BuganairGliderClientState.getRoll()
                ));
            } else {
                ClientPlayNetworking.send(new BuganairGliderPayload(false));
            }
        });

        // ── HUD ───────────────────────────────────────────────────────────────
        HudElementRegistry.addLast(
                Identifier.of(Buganair.MOD_ID, "boat_speed_hud"),
                (drawContext, tickCounter) -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null || client.options.hudHidden) return;

                    if (client.player.getVehicle() instanceof BuganairBoatEntity boat) {
                        Text speedText = Text.literal("Boat speed ")
                                .formatted(Formatting.WHITE)
                                .append(Text.literal("H " + boat.getHorizontalSpeed() + " b/s  ")
                                        .formatted(Formatting.GREEN, Formatting.BOLD))
                                .append(Text.literal("V " + boat.getVerticalSpeed() + " b/s")
                                        .formatted(Formatting.RED, Formatting.BOLD));

                        int x = drawContext.getScaledWindowWidth()  / 2;
                        int y = drawContext.getScaledWindowHeight() - 65;
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

                    int width   = drawContext.getScaledWindowWidth();
                    int height  = drawContext.getScaledWindowHeight();
                    int centerX = width  / 2;
                    int centerY = height / 2;
                    int radius  = Math.min(width, height) / 2 - 20;

                    int blackColor = 0xAA000000;

                    if (centerY - radius > 0) drawContext.fill(0, 0, width, centerY - radius, blackColor);
                    if (centerY + radius < height) drawContext.fill(0, centerY + radius, width, height, blackColor);

                    for (int y = Math.max(0, centerY - radius); y <= Math.min(height, centerY + radius); y++) {
                        double dy = y - centerY;
                        double dx = Math.sqrt((radius * radius) - (dy * dy));
                        int holeLeft  = (int)(centerX - dx);
                        int holeRight = (int)(centerX + dx);
                        if (holeLeft  > 0)     drawContext.fill(0,         y, holeLeft,  y + 1, blackColor);
                        if (holeRight < width) drawContext.fill(holeRight,  y, width,     y + 1, blackColor);
                    }

                    float cooldown = client.player.getItemCooldownManager()
                            .getCooldownProgress(client.player.getMainHandStack(), 0.0f);
                    if (cooldown > 0) {
                        int barWidth  = 60;
                        int barHeight = 4;
                        int x1 = centerX - (barWidth / 2);
                        int y1 = centerY + 30;
                        drawContext.fill(x1, y1, x1 + barWidth, y1 + barHeight, 0x88000000);
                        drawContext.fill(x1, y1, x1 + (int)(barWidth * (1 - cooldown)), y1 + barHeight, 0xFFFF0000);
                    }

                    int color = 0xFF20FF20;
                    drawContext.fill(centerX - 20, centerY,      centerX - 5,  centerY + 1, color);
                    drawContext.fill(centerX + 5,  centerY,      centerX + 20, centerY + 1, color);
                    drawContext.fill(centerX,       centerY - 20, centerX + 1, centerY - 5, color);
                    drawContext.fill(centerX,       centerY + 5,  centerX + 1, centerY + 20, color);

                    int zoom = BuganairSniperClientState.getZoomLevel();
                    net.minecraft.text.Text zoomText = net.minecraft.text.Text
                            .literal("MAG: x" + zoom)
                            .formatted(net.minecraft.util.Formatting.GREEN, net.minecraft.util.Formatting.BOLD);
                    drawContext.drawCenteredTextWithShadow(client.textRenderer, zoomText,
                            centerX, centerY + radius - 25, 0xFFFFFFFF);
                }
        );
    }
}