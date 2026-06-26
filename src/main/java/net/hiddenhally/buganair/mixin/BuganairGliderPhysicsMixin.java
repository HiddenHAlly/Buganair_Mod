package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.Buganair;
import net.hiddenhally.buganair.BuganairMod;
import net.hiddenhally.buganair.BuganairServerGliderState;
import net.hiddenhally.buganair.client.BuganairGliderClientState;
import net.hiddenhally.buganair.config.BuganairConfig;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
            isGliding = BuganairGliderClientState.isGliding() && player.getEquippedStack(EquipmentSlot.CHEST)
                    .isOf(BuganairMod.BUGANAIR_HANG_GLIDER_ITEM);
            pitch = BuganairGliderClientState.getPitch();
            yaw = BuganairGliderClientState.getYaw();
        } else {
            isGliding = BuganairServerGliderState.isGliding(player.getUuid()) && player.getEquippedStack(EquipmentSlot.CHEST)
                    .isOf(BuganairMod.BUGANAIR_HANG_GLIDER_ITEM);
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
        //currentVel = currentVel.add(0.0, -0.08 + liftFactor * 0.06, 0.0);

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

//        // 4. Allineamento del Drift Aerodinamico (Direziona la velocità verso il muso)
//        // Usando direttamente lookX e lookZ senza dividere per 'g', la forza di rotazione
//        // rallenta fluidamente fino a 0 a esattamente 90° e si inverte senza strappi completando il loop.
//        currentVel = currentVel.add((lookX * h - currentVel.x) * 0.1, 0.0, (lookZ * h - currentVel.z) * 0.1);
//
//        // 5. Moltiplicatori di attrito nativi dell'Elytra (0.99 orizzontale, 0.98 verticale)
//        player.setVelocity(currentVel.multiply(0.999, 0.998, 0.999));

        // --- FETCH CUSTOM ENCHANTMENTS ---
        int windRiderLevel = buganair$getEnchantmentLevel(player, BuganairMod.WIND_RIDER);
        int aeroLevel = buganair$getEnchantmentLevel(player, BuganairMod.AERODYNAMIC);
        int thermalLevel = buganair$getEnchantmentLevel(player, BuganairMod.THERMAL_LIFT);

        // 1. Gravità e portanza delle ali (Modified by Wind Rider)
        // Base fall speed is -0.08. Wind Rider reduces this by 0.015 per level (flatter glide)
        double fallSpeed = -0.08 + (windRiderLevel * 2.0/300.0);
        if (fallSpeed > -0.06) {
            fallSpeed = -0.06;
        }
//        if (player.age % 5 == 0) {
//            Buganair.LOGGER.info("{}",liftFactor);
//        }
        currentVel = currentVel.add(0.0, fallSpeed + liftFactor * 0.04, 0.0);

        // ... (Keep your vanilla dive force and climb force logic here) ...

        // 4. Allineamento del Drift Aerodinamico
        currentVel = currentVel.add((lookX * h - currentVel.x) * 0.1, 0.0, (lookZ * h - currentVel.z) * 0.1);

        // --- THERMAL LIFT LOGIC (graduale, non a scatto) ---
        if (thermalLevel > 0) {
            boolean hasHeat = false;
            // 15 block check per tick è trascurabile — rimuovere % 5 per evitare i "polsi"
            for (int i = 1; i <= 15; i++) {
                BlockPos checkPos = player.getBlockPos().down(i);
                var blockState = player.getEntityWorld().getBlockState(checkPos);
                if (blockState.isIn(BlockTags.CAMPFIRES)
                        || blockState.isOf(Blocks.MAGMA_BLOCK)
                        || blockState.isOf(Blocks.FIRE)
                        || blockState.isOf(Blocks.LAVA)) {
                    hasHeat = true;
                    break;
                }
            }
            if (hasHeat) {
                // targetLift = velocità verticale massima che la termica può imprimere
                double targetLift = BuganairConfig.INSTANCE.thermalLiftBoost * thermalLevel;
                // Nudge del 5% verso il target per tick → ~20 tick (1 secondo) per raggiungere la piena portanza
                if (currentVel.y < targetLift) {
                    double nudge = Math.min(targetLift * 0.5, targetLift - currentVel.y);
                    currentVel = currentVel.add(0.0, nudge, 0.0);
                }
            }
        }

        // 5. Moltiplicatori di attrito nativi dell'Elytra (Modified by Aerodynamic)
        // Base friction is 0.999. Aerodynamic pushes it closer to 1.0 (perfect momentum retention)
        double frictionH = 0.999 + (aeroLevel * 0.0003);
        if (frictionH > 1.0) frictionH = 1.0; // Prevent infinite acceleration glitch

        player.setVelocity(currentVel.multiply(frictionH, 0.998, frictionH));

        // Applica il movimento finale al motore di gioco
        player.move(MovementType.SELF, player.getVelocity());

        // Azzera i danni da caduta accumulati durante il volo
        player.fallDistance = 0f;
    }

    // Add this helper method inside your BuganairGliderPhysicsMixin class:
    @Unique
    private int buganair$getEnchantmentLevel(PlayerEntity player, RegistryKey<Enchantment> key) {
        var stack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (stack.isEmpty()) return 0;

        var optionalEnchant = player.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(key);
        if (optionalEnchant.isPresent()) {
            return EnchantmentHelper.getLevel(optionalEnchant.get(), stack);
        }
        return 0;
    }
}