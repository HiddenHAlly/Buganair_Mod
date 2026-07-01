package net.hiddenhally.buganair.worldgen;

import net.hiddenhally.buganair.Buganair;
import net.hiddenhally.buganair.block.BuganairBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.CherryFoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.CherryTrunkPlacer;
import org.apache.commons.codec.language.bm.Rule;

import java.util.List;

/**
 * Registry keys + bootstrap for Buganair's ConfiguredFeatures.
 *
 * ConfiguredFeature = "what does it look like" (tree shape, ore vein shape).
 * PlacedFeature (see BuganairPlacedFeatures) = "where/how often does it generate".
 *
 * TREE SHAPE NOTE: reuses vanilla's CherryTrunkPlacer + CherryFoliagePlacer
 * pair (the exact combo vanilla's own Cherry Blossom tree uses), re-skinned
 * with Skywood log/leaves, to get the "cherry blossom" silhouette you asked
 * for. Trunk/foliage placers are designed as matched pairs in vanilla, so
 * this borrows the pairing wholesale rather than mixing placer types.
 *
 * Verified against yarn 1.21.11 mappings:
 *   CherryTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight,
 *                      IntProvider branchCount, IntProvider branchHorizontalLength,
 *                      UniformIntProvider branchStartOffsetFromTop, IntProvider branchEndOffsetFromTop)
 *   CherryFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height,
 *                        float hangingLeavesChance, float hangingLeavesExtensionChance)
 *   TreeFeatureConfig.Builder(BlockStateProvider trunkProvider, TrunkPlacer trunkPlacer,
 *                              BlockStateProvider foliageProvider, FoliagePlacer foliagePlacer,
 *                              FeatureSize minimumSize)
 */
public class BuganairConfiguredFeatures {

    public static final RegistryKey<ConfiguredFeature<?, ?>> SKYWOOD_TREE =
            key("skywood_tree");

    public static final RegistryKey<ConfiguredFeature<?, ?>> SKY_CRYSTAL_ORE =
            key("sky_crystal_ore");

    /**
     * Bootstrap — called from the datagen RegistryBuilder (see
     * BuganairWorldgenProvider). Populates the dynamic registry with the
     * actual feature configs.
     */
    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {

        // ── Skywood Tree (cherry-style silhouette) ──────────────────────────
        register(
                context,
                SKYWOOD_TREE,
                Feature.TREE,
                new TreeFeatureConfig.Builder(
                        BlockStateProvider.of(BuganairBlocks.SKYWOOD_LOG),
                        // Same numeric defaults vanilla uses for the cherry tree:
                        // short trunk (4 + 0-2 + 0-2 blocks tall), 1-2 branches,
                        // branches reaching 2-4 blocks out, near the top of the trunk.
                        new CherryTrunkPlacer(
                                5, 2, 2,
                                UniformIntProvider.create(1, 3),   // branchCount
                                UniformIntProvider.create(2, 4),   // branchHorizontalLength
                                UniformIntProvider.create(-3, -2), // branchStartOffsetFromTop (must be UniformIntProvider)
                                UniformIntProvider.create(-1, 0)   // branchEndOffsetFromTop
                        ),
                        BlockStateProvider.of(BuganairBlocks.SKYWOOD_LEAVES),
                        // Wide, gently drooping canopy — matches vanilla cherry's silhouette.
                        new CherryFoliagePlacer(
                                UniformIntProvider.create(4, 5), // radius
                                UniformIntProvider.create(0, 0), // offset
                                UniformIntProvider.create(4, 7), // foliage height
                                0.0F,
                                0.0F,
                                0.0F,                             // hangingLeavesChance
                                0.0F                              // hangingLeavesExtensionChance
                        ),
                        new TwoLayersFeatureSize(1, 0, 2)
                )
                        .ignoreVines()
                        .dirtProvider(BlockStateProvider.of(BuganairBlocks.SKYGRASS))
                        //.dirtProvider(BlockStateProvider.of(BuganairBlocks.SKYGRASS.getDefaultState())) // <-- Add this line here
                        .build()
        );

        // ── Sky Crystal Ore ──────────────────────────────────────────────────
        // Small veins (size 6, similar to vanilla iron/copper) that only
        // replace Skystone, since that's the only "stone" sky islands contain
        // — there's no deepslate equivalent up there.
        // ── Sky Crystal Ore ──────────────────────────────────────────────────
        // Small veins (size 6, similar to vanilla iron/copper) that only
        // replace Skystone, since that's the only "stone" sky islands contain
        // — there's no deepslate equivalent up there.
        register(
                context,
                SKY_CRYSTAL_ORE,
                Feature.ORE,
                new OreFeatureConfig(
                        List.of(OreFeatureConfig.createTarget(
                                new BlockMatchRuleTest(BuganairBlocks.SKYSTONE),
                                BuganairBlocks.SKY_CRYSTAL_ORE.getDefaultState()
                        )),
                        6 // vein size
                )
        );
    }

    private static RegistryKey<ConfiguredFeature<?, ?>> key(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Identifier.of(Buganair.MOD_ID, name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(
            Registerable<ConfiguredFeature<?, ?>> context,
            RegistryKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC config
    ) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }
}
