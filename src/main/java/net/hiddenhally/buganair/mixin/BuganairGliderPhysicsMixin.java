package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.BuganairServerGliderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class BuganairGliderPhysicsMixin {

    @Inject(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), cancellable = true)
    private void buganair$gliderTravel(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof ServerPlayerEntity player)) return;

        if (!BuganairServerGliderState.isGliding(player.getUuid())) return;

        if (player.isOnGround()) {
            BuganairServerGliderState.setGliding(player.getUuid(), false);
            return;
        }

        ci.cancel();

        // Read the unconstrained look vectors from your networked server cache
        float pitch = BuganairServerGliderState.getPitch(player.getUuid());
        float yaw = BuganairServerGliderState.getYaw(player.getUuid());

        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        // Pure spherical projection allowing loops past 90 degrees cleanly
        double lookX = -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad);
        double lookY = -MathHelper.sin(pitchRad);
        double lookZ = MathHelper.cos(yawRad) * MathHelper.cos(pitchRad);
        Vec3d lookDir = new Vec3d(lookX, lookY, lookZ).normalize();

        Vec3d currentVel = player.getVelocity();
        double currentSpeed = currentVel.length();
        if (currentSpeed < 0.1) currentSpeed = 0.4;

        // Trade kinetic energy smoothly for altitude during inverted loops
        double acceleration = -lookY * 0.07;
        double drag = 0.012;
        double targetSpeed = currentSpeed + acceleration - drag;

        if (targetSpeed < 0.12) targetSpeed = 0.12;
        if (targetSpeed > 3.0)  targetSpeed = 3.0;

        Vec3d targetVelocity = lookDir.multiply(targetSpeed);

        double lerpH = 0.14;
        double lerpV = 0.09;

        double newX = currentVel.x + (targetVelocity.x - currentVel.x) * lerpH;
        double newY = currentVel.y + (targetVelocity.y - currentVel.y) * lerpV;
        double newZ = currentVel.z + (targetVelocity.z - currentVel.z) * lerpH;

        player.setVelocity(newX, newY, newZ);
        player.move(MovementType.SELF, player.getVelocity());

        player.fallDistance = 0f;
        player.setVelocity(player.getVelocity().multiply(0.995));
    }
}