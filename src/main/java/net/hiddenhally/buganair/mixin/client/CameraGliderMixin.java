package net.hiddenhally.buganair.mixin.client;

import net.hiddenhally.buganair.client.BuganairGliderClientState;
import net.hiddenhally.buganair.item.BuganairHangGliderItem;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraGliderMixin {

    @Shadow private Quaternionf rotation;

    @Inject(method = "update", at = @At("RETURN"))
    private void buganair$applyGliderCameraEffects(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        if (BuganairGliderClientState.isGliding() && BuganairHangGliderItem.isWearingGlider((PlayerEntity) focusedEntity)) {
            // Example: Add a camera banking/roll effect when turning!
            // You can dynamically calculate this float based on player steering/yaw velocity.
            float rollAngle = 0.0f;

            // Multiplies the final camera rotation along the Z-axis (Roll)
            this.rotation.rotateZ((float) Math.toRadians(rollAngle));
        }
    }
}