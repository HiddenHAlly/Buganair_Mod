package net.hiddenhally.buganair.item;

import net.hiddenhally.buganair.BuganairMod;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item; // Extends base Item again


public class BuganairHangGliderItem extends Item {

    public BuganairHangGliderItem(Settings settings) {
        super(settings);
    }

    public static boolean isWearingGlider(PlayerEntity player) {
        return player.getEquippedStack(EquipmentSlot.CHEST)
                .isOf(BuganairMod.BUGANAIR_HANG_GLIDER_ITEM) && player.getEquippedStack(EquipmentSlot.CHEST).getDamage() < player.getEquippedStack(EquipmentSlot.CHEST).getMaxDamage()-1;
    }
}