package net.hiddenhally.buganair.worldgen;

/**
 * Surface rule for the Sky Islands biome: a thin skygrass cap on top,
 * skystone underneath filling out the rest of the island body.
 *
 * BEST-EFFORT NOTE: MaterialRules is Mojang's surface-rule DSL used inside
 * the world preset / noise settings JSON (or its datagen equivalent). This
 * class is NOT wired up automatically — surface rules for a *vanilla-style
 * overworld* are normally defined once, globally, in the worldgen noise
 * settings (minecraft:overworld), keyed off biome tags/conditions, rather
 * than per-biome like ConfiguredFeature/PlacedFeature. To make Skystone
 * actually replace stone specifically inside SKY_ISLANDS, you have two
 * realistic options:
 *
 *   1. (Simpler, recommended) Skip global MaterialRules entirely. Instead,
 *      make Skystone/Skygrass part of your structure/jigsaw pieces or a
 *      dedicated "island blob" Feature (a custom Feature<DefaultFeatureConfig>
 *      that carves a rough sphere/ellipsoid of Skystone with Skygrass on
 *      exposed top faces) placed via a PlacedFeature restricted to your
 *      biome. This avoids touching global noise settings JSON at all and
 *      is much easier to control/tune. I'd suggest this approach in
 *      practice — see BuganairIslandFeature below for a starting point.
 *
 *   2. (Harder) Extend the overworld's noise_settings surface_rule with a
 *      MaterialRules.condition(biome-is(SKY_ISLANDS), MaterialRules.block(...))
 *      branch. This requires either datapack-overriding
 *      data/minecraft/worldgen/noise_settings/overworld.json entirely (risky —
 *      you must reproduce the whole vanilla ruleset and splice yours in) or a
 *      mixin into SurfaceRules / ChunkGenerator. Not recommended unless you
 *      specifically need stone-layer replacement across ALL overworld terrain
 *      conditioned on biome, rather than just your floating islands.
 *
 * Given your actual ask — "an island with skygrass on top, skystone below,
 * ore scattered in, trees on the surface" — option 1 (a custom island-blob
 * feature) is both easier to implement correctly and easier for you to tune
 * later (island size, shape, float height) without fighting vanilla's noise
 * pipeline. I've left this class as documentation of why; see
 * BuganairIslandFeature.java for the actual generator.
 */
public final class BuganairSurfaceRules {
    private BuganairSurfaceRules() {}
}
