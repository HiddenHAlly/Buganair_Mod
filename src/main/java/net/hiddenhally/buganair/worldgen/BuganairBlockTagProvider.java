package net.hiddenhally.buganair.worldgen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.hiddenhally.buganair.block.BuganairBlocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

/**
 * Assigns mining-tool tags (axe/pickaxe) and vanilla behavioral tags to
 * the new Buganair blocks.
 *
 * LEAF DECAY FIX (this pass): Skywood leaves were decaying even when
 * standing right next to Skywood logs. As of 1.20.5+ (including 1.21.11),
 * leaf decay is driven entirely by the #minecraft:prevents_nearby_leaf_decay
 * block tag (populated from vanilla's #minecraft:logs by default) -- it is
 * NOT a hardcoded "any Block instance with a certain shape" check, and
 * custom log blocks are NOT automatically included just because they use
 * PillarBlock or look log-like. Skywood log/wood/stripped variants are
 * added to that tag below, which is the actual, correct fix (no need to
 * hand-manage the leaves' DISTANCE/PERSISTENT blockstate properties
 * yourself -- the tag handles that check for you).
 *
 * API NOTE (per your correction): 1.21.11 uses valueLookupBuilder(...), not
 * getOrCreateTagBuilder(...) -- kept as valueLookupBuilder throughout this file.
 */
public class BuganairBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public BuganairBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        // 1. MINING TOOL TAGS
        valueLookupBuilder(BlockTags.AXE_MINEABLE)
                .add(BuganairBlocks.SKYWOOD_LOG)
                .add(BuganairBlocks.SKYWOOD_WOOD)
                .add(BuganairBlocks.STRIPPED_SKYWOOD_LOG)
                .add(BuganairBlocks.STRIPPED_SKYWOOD_WOOD)
                .add(BuganairBlocks.SKYWOOD_PLANKS);

        valueLookupBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(BuganairBlocks.SKYSTONE)
                .add(BuganairBlocks.SKY_CRYSTAL_ORE);

        valueLookupBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(BuganairBlocks.SKY_CRYSTAL_ORE);

        // 2. THE ABSOLUTE LEAF DECAY FIX (ROOT LOGS TAG)
        valueLookupBuilder(BlockTags.LOGS)
                .add(BuganairBlocks.SKYWOOD_LOG)
                .add(BuganairBlocks.SKYWOOD_WOOD)
                .add(BuganairBlocks.STRIPPED_SKYWOOD_LOG)
                .add(BuganairBlocks.STRIPPED_SKYWOOD_WOOD);

        valueLookupBuilder(BlockTags.LOGS_THAT_BURN)
                .add(BuganairBlocks.SKYWOOD_LOG)
                .add(BuganairBlocks.SKYWOOD_WOOD)
                .add(BuganairBlocks.STRIPPED_SKYWOOD_LOG)
                .add(BuganairBlocks.STRIPPED_SKYWOOD_WOOD);

        valueLookupBuilder(BlockTags.LEAVES)
                .add(BuganairBlocks.SKYWOOD_LEAVES);

        valueLookupBuilder(BlockTags.HOE_MINEABLE)
                .add(BuganairBlocks.SKYWOOD_LEAVES);
    }
}
