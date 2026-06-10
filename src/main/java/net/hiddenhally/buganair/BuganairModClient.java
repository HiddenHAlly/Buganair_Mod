package net.hiddenhally.buganair;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.hiddenhally.buganair.network.BuganairBoatInputPayload;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.BoatEntityRenderer;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class BuganairModClient implements ClientModInitializer {
    private static KeyBinding downKey;

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(BuganairMod.BUGANAIR_BOAT_ENTITY_TYPE, context -> new BoatEntityRenderer(context, false));

        downKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.buganair.boat_down",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            "category.buganair"
        ));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) {
                return;
            }

            if (client.player.getVehicle() instanceof BuganairBoatEntity boat) {
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
                if (downKey.isPressed()) {
                    vertical -= 1;
                }

                boat.setMovementInput(forward, sideways, vertical);
                ClientPlayNetworking.send(new BuganairBoatInputPayload(boat.getId(), forward, sideways, vertical));
            }
        });

    }
}
