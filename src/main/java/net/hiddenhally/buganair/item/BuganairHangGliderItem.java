package net.hiddenhally.buganair.item;

import net.hiddenhally.buganair.BuganairMod;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item; // Extends base Item again
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.function.Consumer;

public class BuganairHangGliderItem extends Item {

    public BuganairHangGliderItem(Settings settings) {
        super(settings);
    }

    public static boolean isWearingGlider(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.CHEST)
                .isOf(BuganairMod.BUGANAIR_HANG_GLIDER_ITEM) && player.getEquippedStack(EquipmentSlot.CHEST).getDamage() < player.getEquippedStack(EquipmentSlot.CHEST).getMaxDamage()-1;
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