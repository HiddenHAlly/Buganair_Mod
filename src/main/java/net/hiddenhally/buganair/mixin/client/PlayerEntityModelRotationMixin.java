package net.hiddenhally.buganair.mixin.client;

import net.hiddenhally.buganair.client.BuganairGliderClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState; // New 1.21.2+ Render State import
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityModelRotationMixin {

    @Inject(method = "setupTransforms*", at = @At("TAIL"))
    private void buganair$applyModelLoopAndRoll(PlayerEntityRenderState state, MatrixStack matrices, float baseAngles, float tickProgress, CallbackInfo ci) {
        // Since we are checking your local client's state, grab the local player instance safely
        ClientPlayerEntity localPlayer = MinecraftClient.getInstance().player;

        if (localPlayer != null && BuganairGliderClientState.isGliding()) {
            // 1. Fetch your unclamped angles from your client state
            float flightPitch = BuganairGliderClientState.getPitch();
            float flightRoll = BuganairGliderClientState.getRoll();

            float j = state.getGlidingProgress();
            float i = state.pitch;

            if (!state.usingRiptide) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-j * (-90.0F - i)));
            }

            // 2. Apply your custom pitch transformation around the X-axis (unclamped)
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j * (-90.0f - flightPitch)));

            // 3. Apply your banking roll transformation around the Z-axis
            //matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(flightRoll));

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-flightRoll));
        }
    }
}