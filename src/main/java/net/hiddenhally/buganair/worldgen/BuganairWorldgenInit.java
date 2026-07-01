package net.hiddenhally.buganair.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.hiddenhally.buganair.Buganair;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;

/**
 * Call BuganairWorldgenInit.init() once from BuganairMod.onInitialize()
 * (common/server init, NOT client-only, NOT datagen).
 *
 * BIOME SCOPING FIX (this pass): switched from
 * BiomeSelectors.tag(BiomeTags.IS_OVERWORLD) -- which still matches nearly
 * every normal overworld biome -- to BiomeSelectors.includeByKey(...) with
 * a small, explicit, curated varargs list (this is the correct call shape
 * -- pass RegistryKey<Biome> values directly, no array wrapper needed).
 * This is what actually gives you control over "only in select biomes"
 * rather than "everywhere, just rarer." Combined with the 1-in-96 rarity
 * in BuganairFeatures, islands should now feel like genuinely uncommon
 * landmarks confined to a handful of biome types.
 *
 * Adjust the biome list below to taste.
 */
public final class BuganairWorldgenInit {

    private BuganairWorldgenInit() {}

    public static void init() {
        Buganair.LOGGER.info("[Buganair] Injecting sky island generation into curated biomes...");

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld().and(BiomeSelectors.includeByKey(
                        BiomeKeys.PLAINS,
                        BiomeKeys.SUNFLOWER_PLAINS,
                        BiomeKeys.FOREST,
                        BiomeKeys.MEADOW
                )),
                GenerationStep.Feature.RAW_GENERATION,
                BuganairFeatures.SKY_ISLAND_PLACED
        );
    }
}
