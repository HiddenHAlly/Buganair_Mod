package net.hiddenhally.buganair.worldgen;

import com.mojang.serialization.Codec;
import net.hiddenhally.buganair.block.BuganairBlocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

import java.util.ArrayList;
import java.util.List;

import static net.hiddenhally.buganair.worldgen.BuganairConfiguredFeatures.SKYWOOD_TREE;

/**
 * Carves a single floating sky island: an ellipsoid blob of Skystone,
 * capped with a thin layer of Skygrass on any exposed top-facing surface,
 * with Sky Crystal Ore sprinkled through the interior, occasional Skywood
 * trees growing directly out of the Skygrass cap, and (rarely, on the
 * biggest islands) an Aether Forge centerpiece.
 *
 * CHUNK-SAFETY FIX (this pass): previous radii (up to 19 blocks) caused
 * "Detected setBlock in a far chunk" errors and could corrupt/freeze
 * generation at chunk boundaries. Minecraft's feature-generation stage
 * only permits setBlockState calls within a small safe margin around the
 * originating chunk -- reaching into a neighboring chunk that hasn't
 * reached the "features" stage yet is invalid. Radii are now capped so the
 * full ellipsoid never exceeds that safe margin: max radius reduced to 12
 * (was up to 19). This is intentionally conservative -- 12 is comfortably
 * within the safe window vanilla itself uses for its own large single-
 * feature placements (e.g. large dripstone). If you want bigger islands
 * later, the correct approach is a proper multi-chunk STRUCTURE (piece-
 * based, chunk-aware) rather than a single Feature -- Features are not
 * meant to exceed roughly one chunk's radius from their origin.
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

        // Fetch the configured features registry and get your Skywood Tree entry
        var configuredFeatureRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.CONFIGURED_FEATURE);
        var treeEntry = configuredFeatureRegistry.getOrThrow(SKYWOOD_TREE);

        // Radii capped at 12 to stay within the safe feature-generation
        // margin around the originating chunk (fixes "far chunk" errors).
        int radiusX = 8 + random.nextInt(5);   // 8-12
        int radiusY = 4 + random.nextInt(3);   // 4-6
        int radiusZ = 8 + random.nextInt(5);   // 8-12

        boolean isCenterpieceCandidate = radiusX >= 11 && radiusZ >= 11;
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

        // 2. Cap exposed top-facing surface blocks with Skygrass, and
        // remember exactly which columns got capped so tree placement
        // (step 5) can ONLY ever use these positions.
        List<int[]> grassCapPositions = new ArrayList<>();

        for (int dx = -radiusX; dx <= radiusX; dx++) {
            for (int dz = -radiusZ; dz <= radiusZ; dz++) {
                for (int dy = radiusY; dy >= -radiusY; dy--) {
                    mutable.set(origin, dx, dy, dz);
                    if (world.getBlockState(mutable).isOf(BuganairBlocks.SKYSTONE)) {
                        BlockPos.Mutable above = mutable.mutableCopy().move(Direction.UP);
                        if (world.getBlockState(above).isAir()) {
                            world.setBlockState(mutable, BuganairBlocks.SKYGRASS.getDefaultState(), 3);
                            grassCapPositions.add(new int[] { dx, dy, dz });
                        }
                        break;
                    }
                }
            }
        }

        // 3. Scatter Sky Crystal Ore through the interior.
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

        // 4. Aether Forge centerpiece on qualifying islands.
        int forgeTopY = Integer.MIN_VALUE;
        if (placeForge) {
            int topY = radiusY;
            for (int dy = radiusY; dy >= -radiusY; dy--) {
                mutable.set(origin, 0, dy, 0);
                if (!world.getBlockState(mutable).isAir()) {
                    topY = dy;
                    break;
                }
            }
            forgeTopY = topY;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    mutable.set(origin, dx, topY, dz);
                    world.setBlockState(mutable, BuganairBlocks.SKYGRASS.getDefaultState(), 3);
                }
            }

            mutable.set(origin, 0, topY + 1, 0);
            world.setBlockState(mutable, BuganairBlocks.AETHER_FORGE.getDefaultState(), 3);
        }

        // 5. Skywood trees, placed directly on this island's own Skygrass
        // cap only. See BuganairBlockTagProvider for the fix that stops
        // these trees' leaves from decaying (prevents_nearby_leaf_decay).
        for (int[] pos : grassCapPositions) {
            int dx = pos[0], dy = pos[1], dz = pos[2];

            if (random.nextFloat() >= 0.05F) continue;

            if (placeForge && dx >= -2 && dx <= 2 && dz >= -2 && dz <= 2 && dy >= forgeTopY - 1) {
                continue;
            }

            double edgeFactor =
                    (double) (dx * dx) / (radiusX * radiusX)
                            + (double) (dz * dz) / (radiusZ * radiusZ);
            if (edgeFactor > 0.7) continue;

            //placeSimpleTree(world, random, origin, dx, dy + 1, dz);
            // Generate the ConfiguredFeature tree at the target offset
            BlockPos treePos = origin.add(dx, dy + 1, dz);
            treeEntry.value().generate(world, context.getGenerator(), random, treePos);
        }

        return true;
    }
}
