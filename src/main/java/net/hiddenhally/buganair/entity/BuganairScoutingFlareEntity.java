package net.hiddenhally.buganair.entity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hiddenhally.buganair.BuganairMod;
import net.hiddenhally.buganair.config.BuganairConfig;
import net.hiddenhally.buganair.network.BuganairScoutingFlareSyncPayload;
import net.minecraft.client.render.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;


public class BuganairScoutingFlareEntity extends ThrownItemEntity {

    public BuganairScoutingFlareEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    // Accept ItemStack here
    public BuganairScoutingFlareEntity(World world, LivingEntity owner) {
        super(BuganairMod.BUGANAIR_SCOUTING_FLARE_ENTITY_TYPE, owner, world, new ItemStack(BuganairMod.BUGANAIR_SCOUTING_FLARE_ITEM));
    }

    @Override
    protected Item getDefaultItem() {
        return BuganairMod.BUGANAIR_SCOUTING_FLARE_ITEM;
    }

    @Override
    public void tick() {
        super.tick();
        // Scia di particelle durante il volo del bengala (solo client)
        if (this.getEntityWorld().isClient()) {
            this.getEntityWorld().addParticleClient(ParticleTypes.SMALL_FLAME, this.getX(), this.getY(), this.getZ(), 0.0, 0.02, 0.0);
            if (this.random.nextFloat() < 0.3f) {
                this.getEntityWorld().addParticleClient(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        // 1. Ensure you are on the logical server side (e.g., inside tick() or onCollision())
        if (this.getEntityWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {

            serverWorld.spawnParticles(
                    ParticleTypes.SMALL_FLAME,                  // The particle type
                    this.getX(), this.getY(), this.getZ(),       // The center position of the burst
                    BuganairConfig.INSTANCE.smallFireParticles,  // Total count (reads directly from your config)
                    0.3, 0.2, 0.3,                              // The horizontal and vertical random spread bounds (X, Y, Z delta)
                    0.02                                        // Particle extra velocity speed (controls how fast they drift outwards)
            );

            serverWorld.spawnParticles(
                    ParticleTypes.SMOKE,                  // The particle type
                    this.getX(), this.getY(), this.getZ(),       // The center position of the burst
                    BuganairConfig.INSTANCE.smokeParticles,  // Total count (reads directly from your config)
                    0.4, 0.3, 0.4,                              // The horizontal and vertical random spread bounds (X, Y, Z delta)
                    0.03                                        // Particle extra velocity speed (controls how fast they drift outwards)
            );

            serverWorld.spawnParticles(
                    ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,                  // The particle type
                    this.getX(), this.getY(), this.getZ(),       // The center position of the burst
                    BuganairConfig.INSTANCE.campfireSignalSmokeParticles,  // Total count (reads directly from your config)
                    0.3, 0.8, 0.3,                              // The horizontal and vertical random spread bounds (X, Y, Z delta)
                    0.04                                        // Particle extra velocity speed (controls how fast they drift outwards)
            );

            serverWorld.spawnParticles(
                    ParticleTypes.EXPLOSION,                  // The particle type
                    this.getX(), this.getY(), this.getZ(),       // The center position of the burst
                    BuganairConfig.INSTANCE.explosionParticles,  // Total count (reads directly from your config)
                    3, 2, 3,                              // The horizontal and vertical random spread bounds (X, Y, Z delta)
                    0.2                                        // Particle extra velocity speed (controls how fast they drift outwards)
            );

            serverWorld.spawnParticles(
                    ParticleTypes.LAVA,                  // The particle type
                    this.getX(), this.getY(), this.getZ(),       // The center position of the burst
                    BuganairConfig.INSTANCE.lavaParticles,  // Total count (reads directly from your config)
                    0.6, 0.6, 0.6,                              // The horizontal and vertical random spread bounds (X, Y, Z delta)
                    0.06                                        // Particle extra velocity speed (controls how fast they drift outwards)
            );
        }

        if (!this.getEntityWorld().isClient()) {
            // Raggio dello scanner (es. 20 blocchi)
            //double radius = 20.0;
            Box scanArea = this.getBoundingBox().expand(BuganairConfig.INSTANCE.entityRadarRadius);

            // Trova tutte le entità viventi nell'area (escludendo chi ha lanciato il bengala se necessario)
            List<LivingEntity> entities = this.getEntityWorld().getEntitiesByClass(LivingEntity.class, scanArea, entity -> entity != this.getOwner());

            for (LivingEntity living : entities) {
                // Applica l'effetto Glowing per 15 secondi (300 tick)
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 300, 0, false, false));
            }


            //new BuganairScoutingFlareSyncPayload(this.getBlockPos());
//            for (int i = 0; i < BuganairConfig.INSTANCE.smallFireParticles; i++) {
//                // Generates a random double value between 0.0 (inclusive) and 1.0 (exclusive)
//                double randomXOffset = this.random.nextDouble();
//                double randomYOffset = this.random.nextDouble();
//                double randomZOffset = this.random.nextDouble();
//
//                this.getEntityWorld().addParticleClient(
//                        ParticleTypes.SMALL_FLAME,
//                        this.getX(),
//                        this.getY(),
//                        this.getZ(),
//                        randomXOffset,
//                        randomYOffset,
//                        randomZOffset
//                );
//            }

            //if (this.random.nextFloat() < 0.3f) {
            //    this.getEntityWorld().addParticleClient(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            //}


            // Suono dell'esplosione del razzo radar
            this.getEntityWorld().playSound(
                    null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS,
                    10.0f, 1.0f
            );

            // 2. Find all server players who have this entity loaded in their render distance
            // (Must be done BEFORE calling this.discard())
            var trackingPlayers = net.fabricmc.fabric.api.networking.v1.PlayerLookup.tracking(this);

            // 3. Loop through those players and send them the visual sync packet
            for (ServerPlayerEntity player : trackingPlayers) {
                ServerPlayNetworking.send(player, new BuganairScoutingFlareSyncPayload(this.getBlockPos(), this.getOwner() == null || this.getOwner() != player));
            }

            // 4. CRITICAL: Now safely remove the entity from the world
            this.discard();
        }

    }
}