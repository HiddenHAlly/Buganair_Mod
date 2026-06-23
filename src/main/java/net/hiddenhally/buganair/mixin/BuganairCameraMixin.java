package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.client.BuganairGliderClientState;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class BuganairCameraMixin {
    @Shadow @Final private Quaternionf rotation;
    @Shadow private float yaw;
    @Shadow private float pitch;

    private float buganair$lastCameraYaw = 0.0f;
    private float buganair$currentRoll = 0.0f;
    private boolean buganair$wasGliding = false;

    @Inject(method = "update", at = @At("TAIL"))
    private void buganair$applyGliderRollAndLoop(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress, CallbackInfo ci) {
        if (focusedEntity instanceof PlayerEntity && BuganairGliderClientState.isGliding()) {
            this.pitch = BuganairGliderClientState.getPitch();
            this.yaw = BuganairGliderClientState.getYaw();

            if (!buganair$wasGliding) {
                buganair$lastCameraYaw = this.yaw;
                buganair$wasGliding = true;
            }

            float yawDelta = this.yaw - buganair$lastCameraYaw;
            if (yawDelta > 180.0f) yawDelta -= 360.0f;
            if (yawDelta < -180.0f) yawDelta += 360.0f;
            buganair$lastCameraYaw = this.yaw;

            float targetRoll = -yawDelta * 3.0f;
            targetRoll = MathHelper.clamp(targetRoll, -45.0f, 45.0f);
            buganair$currentRoll = MathHelper.lerp(0.05f, buganair$currentRoll, targetRoll);

            // Convert to radians for JOML
            float yawRad = (float) Math.toRadians(180-this.yaw);
            float pitchRad = (float) Math.toRadians(this.pitch);
            float rollRad = (float) Math.toRadians(buganair$currentRoll);

            // FIX: Invert pitchRad here to match Minecraft's unconventional inverted coordinate system matrix
            this.rotation.rotationYXZ(yawRad, -pitchRad, rollRad);
        } else {
            if (buganair$wasGliding) {
                buganair$wasGliding = false;
            }
            buganair$currentRoll = MathHelper.lerp(0.12f, buganair$currentRoll, 0.0f);
            if (Math.abs(buganair$currentRoll) < 0.01f) buganair$currentRoll = 0.0f;

            if (buganair$currentRoll != 0.0f) {
                this.rotation.rotateZ((float) Math.toRadians(buganair$currentRoll));
            }
        }
    }
}