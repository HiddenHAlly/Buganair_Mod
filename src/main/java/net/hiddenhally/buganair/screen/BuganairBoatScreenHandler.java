package net.hiddenhally.buganair.screen;

import net.hiddenhally.buganair.BuganairMod; // Import your main mod class
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;

public class BuganairBoatScreenHandler extends GenericContainerScreenHandler {

    public BuganairBoatScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(54));
    }

    public BuganairBoatScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        // Point directly to BuganairMod's registered field
        super(BuganairMod.BUGANAIR_BOAT_SCREEN_HANDLER, syncId, playerInventory, inventory, 6);
    }
}