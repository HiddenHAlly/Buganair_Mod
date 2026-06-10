package net.hiddenhally.buganair;

import net.hiddenhally.buganair.Buganair;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuganairMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        Buganair.LOGGER.info("Mod "+Buganair.MOD_ID+" initialized");
    }
}