package net.hiddenhally.buganair.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.hiddenhally.buganair.Buganair;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BuganairConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "buganair_config.json");

    // ── Sniper: costanti regolabili ──────────────────────────────────────────
    public double SNIPER_ARROW_SPEED = 20.0D;     // blocchi/tick
    public float SNIPER_ARROW_DAMAGE = 3.0F;
    public int SNIPER_FIRE_COOLDOWN_TICKS = 15;  // 0.75s tra un colpo e l'altro

    public int smallFireParticles = 25;
    public int smokeParticles = 40;
    public int campfireSignalSmokeParticles = 60;
    public int explosionParticles = 20;
    public int lavaParticles = 50;

    public int entityRadarRadius = 30;
    public int entityRadarDurationSeconds = 30;
    public int entityRadarCooldownSeconds = 5;
    public float entityRadarExpandTime = 4000f;
    public float entityRadarOutlineSize = 2.0f;
    public int entityOutlineColor = 0xFF00FF00; // Solid Cyan
    public int entityBubbleColor = 0x4400FF00;  // 25% Transparent Cyan
    public int entityEnemyOutlineColor = 0xFFFF0000; // Solid Cyan
    public int entityEnemyBubbleColor = 0x44FF0000;  // 25% Transparent Cyan

    // Default configuration values
    public int radarRadius = 30;
    public int radarDurationSeconds = 30;
    public int radarCooldownSeconds = 10;
    public float radarExpandTime = 4000f;
    public float radarOutlineSize = 2.0f;
    public float outlineSize = 1.0f;

    public float thermalLiftBoost = 0.7f; // ~7 blocks/sec di salita massima, sensazione morbida

    // Format is ARGB: Alpha (Opacity), Red, Green, Blue
    public int outlineColor = 0xFFFF00FF; // Solid Cyan
    public int outlineColorAlphaRemoval = 0x00000000; // To lower the alpha channel of all blocks
    public int outlineColorCoal = 0xFF666666;
    public int outlineColorCopper = 0xFFAA6600;
    public int outlineColorIron = 0xFFAAAAAA;
    public int outlineColorGold = 0xFFAAAA00;
    public int outlineColorRedstone = 0xFFAA0000;
    public int outlineColorLapis = 0xFF0000AA;
    public int outlineColorEmerald = 0xFF00AA00;
    public int outlineColorDiamond = 0xFF00AAAA;
    public int outlineColorNetherGold = 0xFFAA8F00;
    public int outlineColorNetherQuartz = 0xFFCCCCCC;
    public int outlineColorNetherAncientDebris = 0xFFAA3366;
    public int bubbleColor = 0x4400FFFF;  // 25% Transparent Cyan

    public static BuganairConfig INSTANCE = new BuganairConfig();



    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, BuganairConfig.class);
            } catch (IOException e) {
                Buganair.LOGGER.error("Failed to load Buganair config", e);
            }
        } else {
            save(); // Create default config if it doesn't exist
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            Buganair.LOGGER.error("Failed to save Buganair config", e);
        }
    }
}