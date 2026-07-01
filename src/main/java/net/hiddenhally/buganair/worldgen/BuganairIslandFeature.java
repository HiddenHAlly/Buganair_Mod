package net.hiddenhally.buganair.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.hiddenhally.buganair.block.BuganairBlocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

/**
 * Carves a single floating sky island: an ellipsoid blob of Skystone,
 * capped with a thin layer of Skygrass on any exposed top-facing surface,
 * with Sky Crystal Ore sprinkled through the interior.
 *
 * This is a self-contained Feature (doesn't depend on global noise/terrain
 * density functions), so it can be placed at arbitrary Y independent of
 * normal terrain height — which is what actually makes "floating" islands
 * possible without touching the Overworld's noise settings.
 *
 * BEST-EFFORT NOTE: I have not test-compiled this against your exact
 * yarn 1.21.11 Feature<FC> abstract method signature. The shape (extends
 * Feature<DefaultFeatureConfig>, override generate(FeatureContext<...>)
 * returning boolean) has been stable for a long time, but double-check
 * FeatureContext's exact getters (getWorld(), getRandom(), getOrigin())
 * against your mappings.
 *
 * Registration: you'll need to register this as a Feature type (similar to
 * how BuganairBlocks registers blocks) — something like:
 *
 *   public static final Feature<DefaultFeatureConfig> SKY_ISLAND =
 *       Registry.register(Registries.FEATURE, Identifier.of(MOD_ID, "sky_island"),
 *           new BuganairIslandFeature(DefaultFeatureConfig.CODEC));
 *
 * ...then reference Feature.SKY_ISLAND (however you store it) from a
 * ConfiguredFeature + PlacedFeature pair the same way BuganairConfiguredFeatures
 * does for the tree, and add that PlacedFeature to the biome's generation
 * settings under GenerationStep.Feature.RAW_GENERATION or LOCAL_MODIFICATIONS
 * (raw generation is more appropriate here since this feature *is* terrain,
 * not a decoration sitting on top of terrain).
 */
public class BuganairIslandFeature extends Feature<DefaultFeatureConfig> {

    public BuganairIslandFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        var world = context.getWorld();
        Random random = context.getRandom();
        BlockPos origin = context.getOrigin();

        // Random ellipsoid radii — tweak these ranges to taste.
        int radiusX = 6 + random.nextInt(6);   // 6-11
        int radiusY = 3 + random.nextInt(3);   // 3-5 (islands are flatter than they are wide)
        int radiusZ = 6 + random.nextInt(6);   // 6-11

        // "Biggest island gets the Aether Forge" — a single Feature call only
        // ever sees the island it's currently generating (there's no built-in
        // way to compare against other islands placed elsewhere in the world),
        // so we approximate "biggest" as "near the top of this feature's own
        // size range", gated by a chance roll. In practice this means only
        // the largest rolls end up as forge islands, without needing a
        // separate structure system to coordinate across islands. Tune the
        // threshold (9) and chance (0.35F) to taste.
        boolean isCenterpieceCandidate = radiusX >= 9 && radiusZ >= 9;
        boolean placeForge = isCenterpieceCandidate && random.nextFloat() < 0.35F;

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        // 1. Carve the Skystone body (solid ellipsoid).
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dy = -radiusY; dy <= radiusY; dy++) {
                for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                    double normalized =
                            (double) (dx * dx) / (radiusX * radiusX)
                                    + (double) (dy * dy) / (radiusY * radiusY)
                                    + (double) (dz * dz) / (radiusZ * radiusZ);

                    if (normalized <= 1.0) {
                        mutable.set(origin, dx, dy, dz);
                        world.setBlockState(mutable, BuganairBlocks.SKYSTONE.getDefaultState(), 3);
                    }
                }
            }
        }

        // 2. Cap exposed top-facing surface blocks with Skygrass.
        // A block gets grass if it's Skystone and the block directly above
        // it is currently air (i.e. it's a "roof" of the island).
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                for (int dy = radiusY; dy >= -radiusY; dy--) {
                    mutable.set(origin, dx, dy, dz);
                    if (world.getBlockState(mutable).isOf(BuganairBlocks.SKYSTONE)) {
                        BlockPos.Mutable above = mutable.mutableCopy().move(net.minecraft.util.math.Direction.UP);
                        if (world.getBlockState(above).isAir()) {
                            world.setBlockState(mutable, BuganairBlocks.SKYGRASS.getDefaultState(), 3);
                        }
                        break; // only cap the topmost solid block in this column
                    }
                }
            }
        }

        // 3. Scatter Sky Crystal Ore through the interior (small chance per
        // block, only replacing Skystone so we don't punch ore into the
        // grass cap).
        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dy = -radiusY; dy <= radiusY; dy++) {
                for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                    mutable.set(origin, dx, dy, dz);
                    if (world.getBlockState(mutable).isOf(BuganairBlocks.SKYSTONE) && random.nextFloat() < 0.03F) {
                        world.setBlockState(mutable, BuganairBlocks.SKY_CRYSTAL_ORE.getDefaultState(), 3);
                    }
                }
            }
        }

        // 4. If this island rolled as the "centerpiece", place the Aether
        // Forge dead center at the island's peak, sitting on top of the
        // Skygrass cap (dx=0, dz=0 is the origin column).
        if (placeForge) {
            int topY = radiusY;
            for (int dy = radiusY; dy >= -radiusY; dy--) {
                mutable.set(origin, 0, dy, 0);
                if (!world.getBlockState(mutable).isAir()) {
                    topY = dy;
                    break;
                }
            }

            // Flatten a small 3x3 Skygrass platform at the peak so the forge
            // has a clean base, then place the forge one block above it.
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    mutable.set(origin, dx, topY, dz);
                    world.setBlockState(mutable, BuganairBlocks.SKYGRASS.getDefaultState(), 3);
                }
            }

            mutable.set(origin, 0, topY + 1, 0);
            world.setBlockState(mutable, BuganairBlocks.AETHER_FORGE.getDefaultState(), 3);
        }

        return true;
    }
}
