package net.hiddenhally.buganair.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.hiddenhally.buganair.Buganair;
import net.minecraft.world.gen.GenerationStep;

/**
 * Call BuganairWorldgenInit.init() once from BuganairMod.onInitialize()
 * (common/server init, NOT client-only, NOT datagen) — this is what
 * actually makes floating sky islands appear in generated worlds.
 *
 * WHY THIS EXISTS / DESIGN RATIONALE:
 * BuganairIslandFeature places terrain directly (it doesn't rely on
 * SKY_ISLANDS being "chosen" as the biome for a region by the vanilla
 * climate-parameter biome picker). That means we don't need to fight the
 * MultiNoiseBiomeSourceParameterList system at all — we just need
 * BuganairFeatures.SKY_ISLAND_PLACED and BuganairPlacedFeatures
 * .SKYWOOD_TREE_PLACED to be added into the RAW_GENERATION / VEGETAL_DECORATION
 * steps of whatever normal overworld biomes we want islands to float above
 * (plains, forest, etc. — or literally all overworld biomes, your call).
 *
 * The custom SKY_ISLANDS Biome (BuganairBiomes) still exists and is still
 * registered/tagged, but under this design it's not the biome the ground
 * or sky-island blocks actually "belong to" at runtime — it mainly exists
 * as a target for datagen (e.g. if you want a biome-specific ambient sound
 * or want to reference it from a structure's valid-biome list later). If
 * you'd rather islands truly BE the SKY_ISLANDS biome when you fly through
 * them (affecting fog/sky color, music, etc.), that requires the harder
 * MultiNoiseBiomeSourceParameterList route mentioned in
 * BuganairBiomeTagProvider's javadoc — a bigger undertaking than this pass
 * covers, and easy to add later without reworking the island Feature itself.
 *
 * BEST-EFFORT NOTE: BiomeSelectors.foundInOverworld() is the standard
 * predicate for "any biome placed by the overworld biome source" — I
 * believe this method name is correct for 1.21.11 but have not
 * individually re-verified it the way I did the tree feature constructors.
 * If it doesn't compile, check net.fabricmc.fabric.api.biome.v1.BiomeSelectors
 * for the exact current method name (candidates: foundInOverworld(),
 * foundInTheEnd(), tag(TagKey), all()).
 */
public final class BuganairWorldgenInit {

    private BuganairWorldgenInit() {}

    public static void init() {
        Buganair.LOGGER.info("[Buganair] Injecting sky island generation into overworld biomes...");

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.RAW_GENERATION,
                BuganairFeatures.SKY_ISLAND_PLACED
        );

        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.VEGETAL_DECORATION,
                BuganairPlacedFeatures.SKYWOOD_TREE_PLACED
        );
    }
}
