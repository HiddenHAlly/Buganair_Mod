package net.hiddenhally.buganair.worldgen;

import net.hiddenhally.buganair.Buganair;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

/**
 * Registry key + bootstrap for the "Sky Islands" biome.
 *
 * BEST-EFFORT NOTE: Biome.Builder's exact method chain (precipitation type,
 * temperature/downfall floats, BiomeEffects builder fields like fogColor /
 * skyColor / waterColor, and whether "precipitation" takes an enum or a
 * boolean) has shifted across 1.20 -> 1.21 versions. Double check against
 * your yarn 1.21.11 Biome/Biome.Builder/BiomeEffects sources — I've written
 * the version that's been stable since ~1.20 but haven't individually
 * confirmed every setter name for 1.21.11.
 */
public class BuganairBiomes {

    public static final RegistryKey<Biome> SKY_ISLANDS =
            RegistryKey.of(RegistryKeys.BIOME, Identifier.of(Buganair.MOD_ID, "sky_islands"));

    public static void bootstrap(Registerable<Biome> context) {
        var placedFeatures = context.getRegistryLookup(RegistryKeys.PLACED_FEATURE);

        RegistryEntry<PlacedFeature> skywoodTree =
                placedFeatures.getOrThrow(BuganairPlacedFeatures.SKYWOOD_TREE_PLACED);
        RegistryEntry<PlacedFeature> skyIsland =
                placedFeatures.getOrThrow(BuganairFeatures.SKY_ISLAND_PLACED);

        GenerationSettings.LookupBackedBuilder generationSettings =
                new GenerationSettings.LookupBackedBuilder(
                        context.getRegistryLookup(RegistryKeys.PLACED_FEATURE),
                        context.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER)
                );

        // The island itself (Skystone body + Skygrass cap + embedded ore +
        // occasional Aether Forge) is placed as RAW_GENERATION, since it IS
        // terrain rather than a decoration added on top of existing terrain.
        // Ore is generated as PART of the island feature (see
        // BuganairIslandFeature step 3), so BuganairPlacedFeatures.
        // SKY_CRYSTAL_ORE_PLACED is intentionally NOT also registered here
        // -- wiring both in would double up ore density. That PlacedFeature
        // is kept available in case you later want ore veins in a
        // non-floating Skystone terrain variant.
        generationSettings.feature(GenerationStep.Feature.RAW_GENERATION, skyIsland);
        generationSettings.feature(GenerationStep.Feature.VEGETAL_DECORATION, skywoodTree);

        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        // Sparse passive mobs only — sky islands are meant to feel remote.
        spawnSettings.spawn(SpawnGroup.CREATURE, 10, new SpawnSettings.SpawnEntry(net.minecraft.entity.EntityType.BAT, 2, 10));

        context.register(
                SKY_ISLANDS,
                new Biome.Builder()
                        .precipitation(true)
                        .temperature(0.8F)
                        .downfall(0.4F)
                        .effects(
                                new BiomeEffects.Builder()
                                        .waterColor(0x7FCFFF)
                                        //.waterFogColor(0x6FA8DC)
                                        //.fogColor(0xC9E8FF)
                                        //.skyColor(0x87CEFA)
                                        .grassColor(0xE8E8E8)
                                        .foliageColor(0xF5D9FF)
                                        //.moodSound(BiomeMoodSound.CAVE)
                                        .build()
                        )
                        .spawnSettings(spawnSettings.build())
                        .generationSettings(generationSettings.build())
                        .build()
        );
    }
}
