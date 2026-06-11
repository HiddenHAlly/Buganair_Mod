package net.hiddenhally.buganair;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.hiddenhally.buganair.client.BuganairBoatEntityRenderer;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenHandlerType;
import net.hiddenhally.buganair.client.BuganairBoatScreen; // Make sure this matches your package path!
import net.hiddenhally.buganair.network.BuganairBoatInputPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import net.minecraft.util.Colors; // Assicurati di importare questo

public class BuganairModClient implements ClientModInitializer {
    private static KeyBinding horizontalSpeedUpKey;
    private static KeyBinding horizontalSpeedDownKey;
    private static KeyBinding verticalSpeedUpKey;
    private static KeyBinding verticalSpeedDownKey;
    private static final KeyBinding.Category BOAT_CATEGORY = KeyBinding.Category.create(Identifier.of(Buganair.MOD_ID, "buganair"));

    @Override
    public void onInitializeClient() {
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
    }
}
