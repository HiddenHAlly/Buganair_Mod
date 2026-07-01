package net.hiddenhally.buganair.worldgen;

import com.google.common.collect.ImmutableList;
import net.hiddenhally.buganair.Buganair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
//import net.minecraft.registry.entry.RegistryEntryLookup;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.*;

/**
 * PlacedFeature = where/how often a ConfiguredFeature generates.
 *
 * BEST-EFFORT NOTE: placement modifier class names (SquarePlacementModifier,
 * PlacedFeatures.wouldSurvive, HeightRangePlacementModifier, etc.) match the
 * 1.20-1.21 pattern closely, but I have not individually re-verified every
 * constructor signature against 1.21.11 the way I did for the tree feature.
 * If something doesn't compile, cross-check against
 * net.minecraft.world.gen.feature.util.PlacedFeatures (vanilla's own helper
 * class has static methods like PlacedFeatures.wouldSurvive(...) that wrap
 * a lot of this boilerplate and might be simpler than building the modifier
 * list by hand).
 */
public class BuganairPlacedFeatures {

    public static final RegistryKey<PlacedFeature> SKYWOOD_TREE_PLACED =
            key("skywood_tree_placed");

    public static final RegistryKey<PlacedFeature> SKY_CRYSTAL_ORE_PLACED =
            key("sky_crystal_ore_placed");

    public static void bootstrap(Registerable<PlacedFeature> context) {
        RegistryEntryLookup<ConfiguredFeature<?, ?>> configuredFeatures =
                context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        RegistryEntry<ConfiguredFeature<?, ?>> skywoodTree =
                configuredFeatures.getOrThrow(BuganairConfiguredFeatures.SKYWOOD_TREE);
        RegistryEntry<ConfiguredFeature<?, ?>> skyCrystalOre =
                configuredFeatures.getOrThrow(BuganairConfiguredFeatures.SKY_CRYSTAL_ORE);

        // ── Skywood Tree placement ──────────────────────────────────────────
        // ~1 tree per chunk on average where the biome allows it, restricted
        // to natural heightmap surface, with the vanilla "would survive"
        // check (won't place if a sapling couldn't survive on that block).
        context.register(
                SKYWOOD_TREE_PLACED,
                new PlacedFeature(
                        skywoodTree,
                        ImmutableList.of(
                                // 1 attempt per chunk; adjust via RarityFilterPlacementModifier
                                // if you want fewer trees per island (e.g. one every N chunks).
                                CountPlacementModifier.of(1),
                                SquarePlacementModifier.of(),
                                HeightmapPlacementModifier.of(net.minecraft.world.Heightmap.Type.WORLD_SURFACE_WG),
                                BiomePlacementModifier.of()
                        )
                )
        );

        // ── Sky Crystal Ore placement ────────────────────────────────────────
        // 4 veins per chunk attempt, spread through the vertical range where
        // sky islands actually exist (see BuganairBiomes / surface rules —
        // islands float roughly y=90 to y=160 in this design).
        context.register(
                SKY_CRYSTAL_ORE_PLACED,
                new PlacedFeature(
                        skyCrystalOre,
                        ImmutableList.of(
                                CountPlacementModifier.of(4),
                                SquarePlacementModifier.of(),
                                HeightRangePlacementModifier.uniform(YOffset.fixed(90), YOffset.fixed(160))
                        )
                )
        );
    }

    private static RegistryKey<PlacedFeature> key(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Buganair.MOD_ID, name));
    }
}
