package net.hiddenhally.buganair.item;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hiddenhally.buganair.BuganairMod;
import net.hiddenhally.buganair.BuganairServerGliderState;
import net.hiddenhally.buganair.client.BuganairGliderClientState;
import net.hiddenhally.buganair.network.BuganairGliderTogglePayload;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

public class BuganairHangGliderItem extends Item {

    public BuganairHangGliderItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
//        ItemStack stack = user.getStackInHand(hand);
//
//        // Glider can only be deployed mid-air
//        if (!user.isOnGround()) {
//            if (world.isClient()) {
//                // Toggle client physics state
//                BuganairGliderClientState.toggleGliding();
//                // Inform the server about the change
//                ClientPlayNetworking.send(new BuganairGliderTogglePayload(BuganairGliderClientState.isGliding()));
//            }
//            return ActionResult.SUCCESS;
//        }

        return ActionResult.PASS;
    }

    public static boolean isWearingGlider(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.CHEST)
                .isOf(BuganairMod.BUGANAIR_HANG_GLIDER_ITEM);
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context,
                              TooltipDisplayComponent displayComponent,
                              Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.buganair.buganair_hang_glider.tooltip_1")
                .formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable("item.buganair.buganair_hang_glider.tooltip_2")
                .formatted(Formatting.YELLOW));
    }
}