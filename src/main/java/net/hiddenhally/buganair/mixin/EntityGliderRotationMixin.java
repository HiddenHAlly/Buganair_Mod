package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.BuganairServerGliderState;
import net.hiddenhally.buganair.client.BuganairGliderClientState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityGliderRotationMixin {

    @Inject(method = "getRotationVector()Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    private void buganair$overrideGliderRotationVector(CallbackInfoReturnable<Vec3d> cir) {
        Entity self = (Entity) (Object) this;

        // 1. Controllo lato Server (per la fisica gestita dal server e i razzi lato server)
        if (self instanceof ServerPlayerEntity player) {
            if (BuganairServerGliderState.isGliding(player.getUuid())) {
                float pitch = BuganairServerGliderState.getPitch(player.getUuid());
                float yaw = BuganairServerGliderState.getYaw(player.getUuid()); // Assicurati di avere questo getter nel server state

                cir.setReturnValue(buganair$calculateTrueLookVector(pitch, yaw));
            }
        }
        // 2. Controllo lato Client (per predizione locale e particelle dei razzi)
        else if (self instanceof ClientPlayerEntity) {
            if (BuganairGliderClientState.isGliding()) {
                float pitch = BuganairGliderClientState.getPitch();
                float yaw = BuganairGliderClientState.getYaw();

                cir.setReturnValue(buganair$calculateTrueLookVector(pitch, yaw));
            }
        }
    }

    @Unique
    private Vec3d buganair$calculateTrueLookVector(float pitch, float yaw) {
        // Conversione matematica identica a quella vanilla, ma senza il clamp a +-90°
        float f = pitch * ((float)Math.PI / 180F);
        float g = -yaw * ((float)Math.PI / 180F);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d((double)(i * j), (double)(-k), (double)(h * j));
    }
}