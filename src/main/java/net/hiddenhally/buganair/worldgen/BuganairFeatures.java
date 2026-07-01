package net.hiddenhally.buganair.worldgen;

import com.google.common.collect.ImmutableList;
import net.hiddenhally.buganair.Buganair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.RarityFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

/**
 * Registers the BuganairIslandFeature Feature type, plus its
 * ConfiguredFeature + PlacedFeature wrappers.
 *
 * DENSITY FIX (this pass): rarity tightened further, from 1-in-24 to
 * 1-in-96 chunk attempts. 1-in-24 was still producing what read as a
 * "dense mix" -- for reference, that's roughly one attempt per ~1.5
 * regions of 16 chunks, which is fairly frequent for a landmark feature.
 * 1-in-96 is closer to how sparse vanilla's rarer landmark features (e.g.
 * ancient city attempts, ~1-in-100+ scale rarity) feel. Tune further via
 * the constant below -- higher number = rarer.
 *
 * See BuganairWorldgenInit for the biome-scoping fix (switched from the
 * broad #minecraft:is_overworld tag to a small curated biome list).
 */
public class BuganairFeatures {

    /** Tune this for overall island frequency. Higher = rarer. 1-in-N chance per chunk attempt. */
    private static final int ISLAND_RARITY = 96;

    public static final Feature<DefaultFeatureConfig> SKY_ISLAND =
            Registry.register(
                    Registries.FEATURE,
                    Identifier.of(Buganair.MOD_ID, "sky_island"),
                    new BuganairIslandFeature(DefaultFeatureConfig.CODEC)
            );

    public static final RegistryKey<ConfiguredFeature<?, ?>> SKY_ISLAND_CONFIGURED =
            RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(Buganair.MOD_ID, "sky_island"));

    public static final RegistryKey<PlacedFeature> SKY_ISLAND_PLACED =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Buganair.MOD_ID, "sky_island_placed"));

    public static void register() {
        Buganair.LOGGER.info("[Buganair] Registering worldgen features...");
    }

    public static void bootstrapConfigured(Registerable<ConfiguredFeature<?, ?>> context) {
        context.register(
                SKY_ISLAND_CONFIGURED,
                new ConfiguredFeature<>(SKY_ISLAND, DefaultFeatureConfig.INSTANCE)
        );
    }

    /**
     * Places islands high (y=140-190) and rare (1-in-96 chunk attempts).
     * Deliberately no heightmap modifier -- islands must float in open air
     * rather than snapping to terrain surface.
     */
    public static void bootstrapPlaced(Registerable<PlacedFeature> context) {
        var configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
        RegistryEntry<ConfiguredFeature<?, ?>> skyIsland =
                configuredFeatures.getOrThrow(SKY_ISLAND_CONFIGURED);

        context.register(
                SKY_ISLAND_PLACED,
                new PlacedFeature(
                        skyIsland,
                        ImmutableList.of(
                                RarityFilterPlacementModifier.of(ISLAND_RARITY),
                                SquarePlacementModifier.of(),
                                HeightRangePlacementModifier.uniform(YOffset.fixed(140), YOffset.fixed(190)),
                                // FIX: Add biome placement modifier here
                                net.minecraft.world.gen.placementmodifier.BiomePlacementModifier.of()
                        )
                )
        );
    }
}
