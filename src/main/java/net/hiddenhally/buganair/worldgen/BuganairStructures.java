package net.hiddenhally.buganair.worldgen;

import net.hiddenhally.buganair.Buganair;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.StructureTerrainAdaptation;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.structure.DimensionPadding;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.Structure;

import java.util.List;
import java.util.Optional;

/**
 * Registration for the "Aether Forge" structure — a small jigsaw structure
 * that places the AETHER_FORGE block (and, via jigsaw pieces, surrounding
 * platform/decoration blocks) at the centerpiece of the largest generated
 * sky island.
 *
 * BEST-EFFORT NOTE, IMPORTANT: JigsawStructure's constructor has changed
 * shape multiple times across 1.19-1.21 (arguments were bundled into a
 * "Structure.Config" record at one point, then a "JigsawStructure.Builder"
 * pattern (?) may exist depending on version — I have not individually
 * confirmed the 1.21.11 constructor arity/order here). Cross-reference
 * against net.minecraft.world.gen.structure.JigsawStructure in your yarn
 * 1.21.11 sources (or an existing 1.21.11 mod's structure registration,
 * e.g. a trial-chambers-style mod) before assuming this compiles as-is.
 *
 * DESIGN NOTE ON "centerpiece of biggest island": jigsaw structures pick
 * their own placement independent of your BuganairIslandFeature — they
 * don't know about your islands' size/shape. To make the Aether Forge
 * specifically land on the *biggest* island's center as you asked, the
 * more reliable route in practice is:
 *
 *   Option A (simpler): Don't use a jigsaw Structure at all. Instead, make
 *   your BuganairIslandFeature (or a variant of it used for one "special"
 *   island per region) directly place the Aether Forge block plus a small
 *   hand-built platform when it generates an island above some radius
 *   threshold (e.g. radiusX/radiusZ > 9). This guarantees forge-on-biggest-
 *   island without fighting the jigsaw placement/spacing system, and reuses
 *   the same custom-Feature mechanism you already need for the islands
 *   themselves. See BuganairIslandFeature — add a `if (isCenterpieceIsland)`
 *   branch there.
 *
 *   Option B (this file): a true jigsaw Structure, registered normally with
 *   StructureSet spacing/separation, and gated with a biome check so it can
 *   only ever land in SKY_ISLANDS. This is more "vanilla-idiomatic" (you get
 *   a real structure with pieces, jigsaw pool randomization, etc.) but does
 *   NOT inherently know about island size — it'll pick its own spawn chunk
 *   per the StructureSet's spacing/separation/salt, independent of which
 *   island happens to be biggest nearby.
 *
 * Given you explicitly want it on the biggest island specifically, Option A
 * is the one that actually delivers what you asked. I'm including this
 * jigsaw scaffold (Option B) as well since you said you plan to "place a
 * new custom structure" there later — a jigsaw Structure is the right tool
 * once you're building multi-piece structures, just not for the "guaranteed
 * on the biggest island" placement rule specifically.
 */
public class BuganairStructures {

    public static final RegistryKey<Structure> AETHER_FORGE_STRUCTURE =
            RegistryKey.of(RegistryKeys.STRUCTURE, Identifier.of(Buganair.MOD_ID, "aether_forge"));

    public static void bootstrap(Registerable<Structure> context) {
        var biomes = context.getRegistryLookup(RegistryKeys.BIOME);
        var templatePools = context.getRegistryLookup(RegistryKeys.TEMPLATE_POOL);

        RegistryEntryList<Biome> validBiomes =
                RegistryEntryList.of(biomes.getOrThrow(BuganairBiomes.SKY_ISLANDS));

        RegistryEntry<StructurePool> startPool =
                templatePools.getOrThrow(
                        RegistryKey.of(RegistryKeys.TEMPLATE_POOL,
                                Identifier.of(Buganair.MOD_ID, "aether_forge/start_pool"))
                );

        context.register(
                AETHER_FORGE_STRUCTURE,
                new JigsawStructure(
                        new Structure.Config(
                                validBiomes,
                                java.util.Map.of(),
                                net.minecraft.world.gen.GenerationStep.Feature.SURFACE_STRUCTURES,
                                StructureTerrainAdaptation.NONE
                        ),
                        startPool,
                        Optional.empty(), // startJigsawName
                        1,                // size (max jigsaw recursion depth)
                        ConstantHeightProvider.create(YOffset.fixed(0)), // startHeight
                        false,            // useExpansionHack
                        Optional.empty(), // projectStartToHeightmap
                        new JigsawStructure.MaxDistanceFromCenter(80),   // Instantiate record via new
                        List.of(),        // poolAliasBindings
                        DimensionPadding.NONE,  // Uses NONE in Yarn mappings
                        StructureLiquidSettings.IGNORE_WATERLOGGING // Uses IGNORE_WATERLOGGING
                )
        );
    }
}
