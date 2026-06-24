package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.BuganairServerGliderState;
import net.hiddenhally.buganair.client.BuganairGliderClientState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Porting 1:1 della fisica Elytra di Minecraft 1.21.x modificata per supportare i 360° continui.
 * Elimina la singolarità a ±90° rimuovendo la divisione per la magnitudo orizzontale 'g'.
 */
@Mixin(LivingEntity.class)
public abstract class BuganairGliderPhysicsMixin {

    @Inject(method = "travel(Lnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), cancellable = true)
    private void buganair$gliderTravel(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof PlayerEntity player)) return;

        boolean isClient = player.getEntityWorld().isClient();
        boolean isGliding;
        float pitch;
        float yaw;

        // Recupera gli angoli non vincolati dallo stato corretto
        if (isClient) {
            isGliding = BuganairGliderClientState.isGliding();
            pitch = BuganairGliderClientState.getPitch();
            yaw = BuganairGliderClientState.getYaw();
        } else {
            isGliding = BuganairServerGliderState.isGliding(player.getUuid());
            pitch = BuganairServerGliderState.getPitch(player.getUuid());
            yaw = BuganairServerGliderState.getYaw(player.getUuid());
        }

        if (!isGliding) return;

        // Atterraggio automatico
        if (player.isOnGround()) {
            if (isClient) {
                BuganairGliderClientState.setGliding(false);
            } else {
                BuganairServerGliderState.setGliding(player.getUuid(), false);
            }
            return;
        }

        // Interrompe completamente la pipeline di movimento standard
        ci.cancel();

        // Conversione in radianti
        float pitchRad = pitch * 0.017453292F;
        float yawRad = yaw * 0.017453292F;

        // Calcolo delle componenti pure del vettore di sguardo (Formule Vanilla)
        double lookX = -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad);
        double lookY = -MathHelper.sin(pitchRad);
        double lookZ = MathHelper.cos(yawRad) * MathHelper.cos(pitchRad);

        Vec3d currentVel = player.getVelocity();
        double h = currentVel.horizontalLength(); // Velocità orizzontale attuale

        // liftFactor = cos^2(pitch). Nello spazio 3D continuo equivale a lookX^2 + lookZ^2
        double liftFactor = lookX * lookX + lookZ * lookZ;

        // 1. Gravità e portanza delle ali (Formule e costanti Vanilla identiche)
        currentVel = currentVel.add(0.0, -0.08 + liftFactor * 0.06, 0.0);

        // 2. Fisica di Picchiata Vanilla: aumenta la spinta quando si cade guardando in basso
        if (currentVel.y < 0.0) {
            double diveForce = currentVel.y * -0.2 * liftFactor;
            // Sostituito lookX/g con lookX per eliminare il salto di 180° a testa in giù
            currentVel = currentVel.add(lookX * diveForce, diveForce, lookZ * diveForce);
        }

        // 3. Fisica di Cabrata Vanilla: converte velocità orizzontale in quota quando si guarda in alto (lookY > 0)
        if (lookY > 0.0) {
            double climbForce = h * lookY * 0.04;
            // Sostituito -lookX/g con -lookX per rendere la transizione oltre i 90° lineare
            currentVel = currentVel.add(-lookX * climbForce, climbForce * 3.2, -lookZ * climbForce);
        }

        // 4. Allineamento del Drift Aerodinamico (Direziona la velocità verso il muso)
        // Usando direttamente lookX e lookZ senza dividere per 'g', la forza di rotazione
        // rallenta fluidamente fino a 0 a esattamente 90° e si inverte senza strappi completando il loop.
        currentVel = currentVel.add((lookX * h - currentVel.x) * 0.1, 0.0, (lookZ * h - currentVel.z) * 0.1);

        // 5. Moltiplicatori di attrito nativi dell'Elytra (0.99 orizzontale, 0.98 verticale)
        player.setVelocity(currentVel.multiply(0.999, 0.998, 0.999));

        // Applica il movimento finale al motore di gioco
        player.move(MovementType.SELF, player.getVelocity());

        // Azzera i danni da caduta accumulati durante il volo
        player.fallDistance = 0f;
    }
}