package net.hiddenhally.buganair.item;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hiddenhally.buganair.config.BuganairConfig;
import net.hiddenhally.buganair.entity.BuganairScoutingFlareEntity;
import net.hiddenhally.buganair.network.BuganairRadarSyncPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class BuganairScoutingFlareItem extends Item {
    public BuganairScoutingFlareItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

//        if (!world.isClient()) {
//            // Set cooldown based on config (ticks = seconds * 20)
//            user.getItemCooldownManager().set(itemStack, BuganairConfig.INSTANCE.entityRadarCooldownSeconds * 20);
//
//            // Notify the client to start the radar visual effect
//            //ServerPlayNetworking.send((ServerPlayerEntity) user, new BuganairRadarSyncPayload(user.getBlockPos()));
//        }

        world.playSound(
                null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL,
                0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
        );

        if (!world.isClient()) {
            BuganairScoutingFlareEntity flare = new BuganairScoutingFlareEntity(world, user);
            // Definisce traiettoria, direzione e potenza del lancio
            flare.setVelocity(user, user.getPitch(), user.getYaw(), 0.0f, 1.5f, 1.0f);
            world.spawnEntity(flare);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return ActionResult.SUCCESS;
    }
}