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
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

/**
 * Registers the BuganairIslandFeature Feature type itself (a Feature must be
 * registered in Registries.FEATURE, separately from its ConfiguredFeature/
 * PlacedFeature wrappers), plus the ConfiguredFeature + PlacedFeature that
 * actually spawns floating islands.
 *
 * Call BuganairFeatures.register() once, early, from BuganairMod.onInitialize()
 * — the same place you'd call BuganairBlocks.register() — so the Feature
 * instance exists before datagen or world load tries to reference it.
 */
public class BuganairFeatures {

    /** The raw Feature implementation, registered under Registries.FEATURE. */
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

    /** Trivial init hook — ensures the static SKY_ISLAND field above is loaded. */
    public static void register() {
        Buganair.LOGGER.info("[Buganair] Registering worldgen features...");
    }

    /**
     * Bootstrap for the ConfiguredFeature registry. Call from your
     * datagen RegistryBuilder alongside BuganairConfiguredFeatures.bootstrap().
     */
    public static void bootstrapConfigured(Registerable<ConfiguredFeature<?, ?>> context) {
        context.register(
                SKY_ISLAND_CONFIGURED,
                new ConfiguredFeature<>(SKY_ISLAND, DefaultFeatureConfig.INSTANCE)
        );
    }

    /**
     * Bootstrap for the PlacedFeature registry. Call from your datagen
     * RegistryBuilder alongside BuganairPlacedFeatures.bootstrap().
     *
     * Places islands scattered vertically between y=90 and y=160, roughly
     * 1 attempt per chunk (tune CountPlacementModifier for density — lower
     * for sparser skies). NOTE: deliberately does NOT use a heightmap
     * placement modifier, since islands must be able to spawn floating in
     * open air rather than snapping to existing terrain surface.
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
                                CountPlacementModifier.of(1),
                                SquarePlacementModifier.of(),
                                HeightRangePlacementModifier.uniform(YOffset.fixed(90), YOffset.fixed(160)),
                                // FIX: Add biome placement modifier here
                                net.minecraft.world.gen.placementmodifier.BiomePlacementModifier.of()
                        )
                )
        );
    }
}
