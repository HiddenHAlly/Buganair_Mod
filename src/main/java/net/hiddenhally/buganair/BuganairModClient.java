package net.hiddenhally.buganair;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.hiddenhally.buganair.network.BuganairBoatInputPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class BuganairModClient implements ClientModInitializer {
    private static KeyBinding horizontalSpeedUpKey;
    private static KeyBinding horizontalSpeedDownKey;
    private static KeyBinding verticalSpeedUpKey;
    private static KeyBinding verticalSpeedDownKey;

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(BuganairMod.BUGANAIR_BOAT_ENTITY_TYPE, context -> new BoatEntityRenderer(context, false));

        horizontalSpeedUpKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.buganair.boat_speed_horizontal_up",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
            "category.buganair"
        ));

        horizontalSpeedDownKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.buganair.boat_speed_horizontal_down",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
            "category.buganair"
        ));

        verticalSpeedUpKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.buganair.boat_speed_vertical_up",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT,
            "category.buganair"
        ));

        verticalSpeedDownKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.buganair.boat_speed_vertical_down",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT,
            "category.buganair"
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

        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) {
                return;
            }

            if (client.player.getVehicle() instanceof BuganairBoatEntity boat) {
                String speedText = "Boat speed §a§lH " + boat.getHorizontalSpeed() + " b/s  §4§lV " + boat.getVerticalSpeed() + " b/s";
                int x = drawContext.getScaledWindowWidth() / 2;
                int y = drawContext.getScaledWindowHeight() - 59;
                drawContext.drawCenteredTextWithShadow(client.textRenderer, Text.literal(speedText), x, y, 0xFFFFFF);
            }
        });
    }
}
