package net.hiddenhally.buganair.worldgen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.hiddenhally.buganair.block.BuganairBlocks;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/**
 * Loot tables for the new blocks. Most are simple "drop self" — Skywood
 * logs/planks/skystone/aether_forge. Leaves and ore get special handling.
 *
 * BEST-EFFORT NOTE: FabricBlockLootTableProvider's exact helper method
 * names (addDrop, dropsWithSilkTouch, addLeavesDrops, etc.) have been
 * fairly stable, but I have not individually re-verified addLeavesDrops'
 * signature against 1.21.11. If it doesn't compile, check
 * net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
 * for the current method name/signature — worst case, build the leaves
 * loot table manually with LootTable.builder() the way vanilla's
 * BlockLootTableGenerator does for OAK_LEAVES.
 *
 * SKY_CRYSTAL_ORE currently drops itself (via the default addDrop in the
 * final else-branch)  you'll likely want a dedicated "Sky Crystal" item
 * drop instead once you've registered one; swap the addDrop(SKY_CRYSTAL_ORE)
 * call for something like addDrop(SKY_CRYSTAL_ORE, oreDrops(SKY_CRYSTAL_ORE,
 * BuganairItems.SKY_CRYSTAL)) once that item exists.
 */
public class BuganairBlockLootTableProvider extends FabricBlockLootTableProvider {

    protected BuganairBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        // Simple "drop self" blocks.
        addDrop(BuganairBlocks.SKYWOOD_LOG);
        addDrop(BuganairBlocks.SKYWOOD_WOOD);
        addDrop(BuganairBlocks.STRIPPED_SKYWOOD_LOG);
        addDrop(BuganairBlocks.STRIPPED_SKYWOOD_WOOD);
        addDrop(BuganairBlocks.SKYWOOD_PLANKS);
        addDrop(BuganairBlocks.SKYSTONE);
        addDrop(BuganairBlocks.SKYSTONE_STAIRS);
        addDrop(BuganairBlocks.AETHER_FORGE);
        addDrop(BuganairBlocks.SKYGRASS);

        // Slabs need the vanilla "double slab drops 2 items" helper.
        addDrop(BuganairBlocks.SKYSTONE_SLAB, slabDrops(BuganairBlocks.SKYSTONE_SLAB));

        // Leaves: vanilla-style sapling/stick chance table. If you haven't
        // registered a Skywood sapling item/block yet, swap the first
        // argument for null-safe handling or just addDrop(SKYWOOD_LEAVES)
        // as a placeholder until the sapling exists.
        // addLeavesDrops(BuganairBlocks.SKYWOOD_LEAVES, BuganairBlocks.SKYWOOD_SAPLING, NORMAL_LEAVES_STICK_CHANCE);
        addDrop(BuganairBlocks.SKYWOOD_LEAVES);

        // Ore — currently drops itself; see class javadoc for swapping to a
        // dedicated Sky Crystal item once registered.
        addDrop(BuganairBlocks.SKY_CRYSTAL_ORE);
    }
}
