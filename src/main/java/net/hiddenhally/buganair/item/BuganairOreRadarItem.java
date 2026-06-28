package net.hiddenhally.buganair.item;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hiddenhally.buganair.config.BuganairConfig;
import net.hiddenhally.buganair.network.BuganairRadarSyncPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BuganairOreRadarItem extends Item {
    public BuganairOreRadarItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            // Set cooldown based on config (ticks = seconds * 20)
            user.getItemCooldownManager().set(stack, BuganairConfig.INSTANCE.radarCooldownSeconds * 20);

            // Notify the client to start the radar visual effect
            ServerPlayNetworking.send((ServerPlayerEntity) user, new BuganairRadarSyncPayload(user.getBlockPos()));
        }

        return ActionResult.SUCCESS;
    }
}