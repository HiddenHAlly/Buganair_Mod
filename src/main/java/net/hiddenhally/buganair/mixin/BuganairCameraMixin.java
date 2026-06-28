package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.Buganair;
import net.hiddenhally.buganair.BuganairMod;
import net.hiddenhally.buganair.client.BuganairGliderClientState;
import net.hiddenhally.buganair.client.BuganairSniperClientState;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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

    // Shadow the positioning methods needed to rewrite third-person tracking
    @Shadow protected abstract void setPos(double x, double y, double z);
    @Shadow protected abstract void moveBy(float surge, float heave, float sway);
    @Shadow protected abstract float clipToSpace(float distance);

    @Shadow
    @Final
    private Vector3f horizontalPlane;
    @Shadow
    private World area;
    @Shadow
    private Entity focusedEntity;
    private float buganair$lastCameraYaw = 0.0f;
    private float buganair$currentRoll = 0.0f;
    private boolean buganair$wasGliding = false;
    private float buganair$currentXOffset = 0.0f;
    private float safePeekDistance;
    private Vec3d pos;

    private float clipCameraToSpace(Entity focusedEntity, float distance, float tickProgress, Vec3d direction) {
        // Determine the base eye position of the player
        double eyeX = MathHelper.lerp((double)tickProgress, focusedEntity.lastX, focusedEntity.getX());
        double eyeY = MathHelper.lerp((double)tickProgress, focusedEntity.lastY, focusedEntity.getY()) + (double)focusedEntity.getStandingEyeHeight();
        double eyeZ = MathHelper.lerp((double)tickProgress, focusedEntity.lastZ, focusedEntity.getZ());
        this.pos = new Vec3d(eyeX, eyeY, eyeZ);

        // Normalize the input direction to ensure accurate distance tracking
        Vec3d unitDirection = direction.normalize();

        // Raycast an 8-point bounding box cluster to prevent near-plane clipping
        for (int i = 0; i < 8; i++) {
            float g = (i & 1) * 2 - 1;
            float h = (i >> 1 & 1) * 2 - 1;
            float j = (i >> 2 & 1) * 2 - 1;
            Vec3d vec3d = this.pos.add(g * 0.1F, h * 0.1F, j * 0.1F);

            // MODIFIED: Project the end point along your custom direction vector
            Vec3d vec3d2 = vec3d.add(unitDirection.multiply(distance));

            HitResult hitResult = this.area.raycast(new RaycastContext(
                    vec3d, vec3d2, RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.NONE, focusedEntity
            ));

            if (hitResult.getType() != HitResult.Type.MISS) {
                float k = (float)hitResult.getPos().squaredDistanceTo(this.pos);
                if (k < MathHelper.square(distance)) {
                    distance = MathHelper.sqrt(k);
                }
            }
        }

        return Math.max(distance - 0.15f, 0.0f);
    }

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

            float currentRoll = BuganairGliderClientState.getRoll() - yawDelta * 3.0f;
            float targetRoll = MathHelper.clamp(currentRoll, -45.0f, 45.0f);
            buganair$currentRoll = MathHelper.lerp(0.05f, buganair$currentRoll, targetRoll);

            // Convert to radians for JOML
            float yawRad = (float) Math.toRadians(180 - this.yaw);
            float pitchRad = (float) Math.toRadians(this.pitch);
            float rollRad = (float) Math.toRadians(buganair$currentRoll);

            // 1. Set the clean forward-facing flight rotation matrix first
            this.rotation.rotationYXZ(yawRad, -pitchRad, rollRad);


            // 2. CRITICAL FIX: Manually calculate camera position using our unconstrained angles
            if (thirdPerson) {
                // Determine the base eye position of the player
                double eyeX = MathHelper.lerp((double)tickProgress, focusedEntity.lastX, focusedEntity.getX());
                double eyeY = MathHelper.lerp((double)tickProgress, focusedEntity.lastY, focusedEntity.getY()) + (double)focusedEntity.getStandingEyeHeight();
                double eyeZ = MathHelper.lerp((double)tickProgress, focusedEntity.lastZ, focusedEntity.getZ());

                // Reset camera back to player eyes
                this.setPos(eyeX, eyeY, eyeZ);

                // Move backward relative to our updated 360-degree matrix (accounting for block collisions)
                double dist = this.clipToSpace(4.0f);
                this.moveBy((float) -dist, 0.0f, 0.0f);
            }

            // 3. If in third person FRONT, flip the camera rotation afterward to look back at the player
            if (inverseView) {
                // Determine the base eye position of the player
                double eyeX = MathHelper.lerp((double)tickProgress, focusedEntity.lastX, focusedEntity.getX());
                double eyeY = MathHelper.lerp((double)tickProgress, focusedEntity.lastY, focusedEntity.getY()) + (double)focusedEntity.getStandingEyeHeight();
                double eyeZ = MathHelper.lerp((double)tickProgress, focusedEntity.lastZ, focusedEntity.getZ());

                // Reset camera back to player eyes
                this.setPos(eyeX, eyeY, eyeZ);

                // Move backward relative to our updated 360-degree matrix (accounting for block collisions)
                double dist = this.clipToSpace(4.0f);
                this.moveBy((float) dist, 0.0f, 0.0f);

                yawRad += (float) Math.PI;
                pitchRad = -pitchRad;
                rollRad = -rollRad;
                this.rotation.rotationYXZ(yawRad, -pitchRad, rollRad);
            }
        } else {
            if (buganair$wasGliding) {
                buganair$wasGliding = false;
            }
            if (focusedEntity instanceof PlayerEntity && (((PlayerEntity) focusedEntity).getMainHandStack().isOf(BuganairMod.BUGANAIR_SNIPER_ITEM) || ((PlayerEntity) focusedEntity).getOffHandStack().isOf(BuganairMod.BUGANAIR_SNIPER_ITEM))) {
                this.pitch = focusedEntity.getPitch();
                this.yaw = focusedEntity.getYaw();
                this.buganair$currentXOffset = BuganairSniperClientState.getXOffset();

                //if (!buganair$wasGliding) {
                    buganair$lastCameraYaw = this.yaw;
                    //buganair$wasGliding = true;
                //}

                float yawDelta = this.yaw - buganair$lastCameraYaw;
                if (yawDelta > 180.0f) yawDelta -= 360.0f;
                if (yawDelta < -180.0f) yawDelta += 360.0f;
                buganair$lastCameraYaw = this.yaw;

                float currentRoll = BuganairSniperClientState.getRoll();
                float targetRoll = MathHelper.clamp(currentRoll, -45.0f, 45.0f);
                buganair$currentRoll = MathHelper.lerp(0.05f, buganair$currentRoll, targetRoll);

                // Convert to radians for JOML
                float yawRad = (float) Math.toRadians(180 - this.yaw);
                float pitchRad = (float) Math.toRadians(this.pitch);
                float rollRad = (float) Math.toRadians(buganair$currentRoll);

                // Determine the base eye position of the player
                double eyeX = MathHelper.lerp((double)tickProgress, focusedEntity.lastX, focusedEntity.getX());
                double eyeY = MathHelper.lerp((double)tickProgress, focusedEntity.lastY, focusedEntity.getY()) + (double)focusedEntity.getStandingEyeHeight();
                double eyeZ = MathHelper.lerp((double)tickProgress, focusedEntity.lastZ, focusedEntity.getZ());
                // Reset camera back to player eyes
                this.setPos(eyeX, eyeY, eyeZ);

                // Create a vector pointing directly to the player's right
                Vec3d lookVector = focusedEntity.getRotationVec(tickProgress);
                Vec3d upVector = new Vec3d(0.0, 1.0, 0.0);
                Vec3d rightDirection = lookVector.crossProduct(upVector).normalize();

                // If leaning left, simply negate it:
                Vec3d leftDirection = rightDirection.negate();


                if (buganair$currentXOffset >= 0) {
                    // Check if the peek width clears geometry blocks
                    safePeekDistance = clipCameraToSpace(focusedEntity, Math.abs(BuganairSniperClientState.getXOffset()), tickProgress, rightDirection);
                } else {
                    // Check if the peek width clears geometry blocks
                    safePeekDistance = -clipCameraToSpace(focusedEntity, Math.abs(BuganairSniperClientState.getXOffset()), tickProgress, leftDirection);
                }

                this.moveBy(0.0f,0.0f, safePeekDistance);

                // 1. Set the clean forward-facing flight rotation matrix first
                this.rotation.rotationYXZ(yawRad, -pitchRad, rollRad);

                // 2. CRITICAL FIX: Manually calculate camera position using our unconstrained angles
                if (thirdPerson) {
                    // Determine the base eye position of the player
                    eyeX = MathHelper.lerp((double)tickProgress, focusedEntity.lastX, focusedEntity.getX());
                    eyeY = MathHelper.lerp((double)tickProgress, focusedEntity.lastY, focusedEntity.getY()) + (double)focusedEntity.getStandingEyeHeight();
                    eyeZ = MathHelper.lerp((double)tickProgress, focusedEntity.lastZ, focusedEntity.getZ());

                    // Reset camera back to player eyes
                    this.setPos(eyeX, eyeY, eyeZ);

                    // Move backward relative to our updated 360-degree matrix (accounting for block collisions)
                    Vec3d forwardDirection = new Vec3d(this.horizontalPlane);
                    float safeDistance = clipCameraToSpace(focusedEntity, 4.0f, tickProgress, forwardDirection);
                    this.moveBy((float) -safeDistance, 0.0f, safePeekDistance);
                }

                // 3. If in third person FRONT, flip the camera rotation afterward to look back at the player
                if (inverseView) {
                    // Determine the base eye position of the player
                    eyeX = MathHelper.lerp((double) tickProgress, focusedEntity.lastX, focusedEntity.getX());
                    eyeY = MathHelper.lerp((double) tickProgress, focusedEntity.lastY, focusedEntity.getY()) + (double) focusedEntity.getStandingEyeHeight();
                    eyeZ = MathHelper.lerp((double) tickProgress, focusedEntity.lastZ, focusedEntity.getZ());

                    // Reset camera back to player eyes
                    this.setPos(eyeX, eyeY, eyeZ);

                    Vec3d backwardDirection = new Vec3d(this.horizontalPlane).negate();
                    float safeDistance = clipCameraToSpace(focusedEntity, 4.0f, tickProgress, backwardDirection);

                    // Move backward relative to our updated 360-degree matrix (accounting for block collisions)
                    //double dist = this.clipToSpace(4.0f);
                    this.moveBy((float) safeDistance, 0.0f, safePeekDistance);

                    yawRad += (float) Math.PI;
                    pitchRad = -pitchRad;
                    rollRad = -rollRad;
                    this.rotation.rotationYXZ(yawRad, -pitchRad, rollRad);
                }

                // Buganair.LOGGER.info("{}",distX);
            } else {
                buganair$currentRoll = MathHelper.lerp(0.12f*3.0f, buganair$currentRoll, 0.0f);
                if (Math.abs(buganair$currentRoll) < 0.01f) buganair$currentRoll = 0.0f;

                if (buganair$currentRoll != 0.0f) {
                    this.rotation.rotateZ((float) Math.toRadians(buganair$currentRoll));
                }
            }
        }
    }
}