package net.hiddenhally.buganair.item;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hiddenhally.buganair.config.BuganairConfig;
import net.hiddenhally.buganair.network.BuganairRadarSyncPayload;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

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

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context,
                              TooltipDisplayComponent displayComponent,
                              Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.buganair.buganair_ore_radar.tooltip_1")
                .formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable("item.buganair.buganair_ore_radar.tooltip_2")
                .formatted(Formatting.YELLOW));
    }
}