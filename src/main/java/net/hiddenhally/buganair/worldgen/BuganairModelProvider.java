package net.hiddenhally.buganair.worldgen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.hiddenhally.buganair.block.BuganairBlocks;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.TexturedModel;

/**
 * Generates blockstate JSON + block/item models for the new Buganair blocks.
 *
 * REQUIRES TEXTURES: this provider assumes textures already exist at
 *   src/main/resources/assets/buganair/textures/block/<name>.png
 * for every block name below (skywood_log, skywood_log_top,
 * stripped_skywood_log, stripped_skywood_log_top, skywood_planks,
 * skywood_leaves, skystone, sky_crystal_ore, aether_forge front/side/top,
 * skygrass). I have NOT created placeholder textures — none are included
 * in this pass. Without them, datagen will still emit valid JSON, but the
 * game will show missing-texture (purple/black) blocks until you add PNGs.
 *
 * BEST-EFFORT NOTE: BlockStateModelGenerator's helper method names shift a
 * bit release to release. The calls below follow the pattern that's been
 * stable roughly 1.20-1.21, but I have not individually re-verified each
 * one against 1.21.11. For anything that doesn't compile, cross-check
 * against net.minecraft.client.data.BlockStateModelGenerator in your yarn
 * 1.21.11 sources — every vanilla block gives you a working example to
 * copy (e.g. OAK_LOG, OAK_LEAVES, COBBLESTONE, IRON_ORE, and the FURNACE's
 * lit/unlit state, in BlockStateModelGenerator.registerBlockStates()).
 */
public class BuganairModelProvider extends FabricModelProvider {

    public BuganairModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
//        // Logs: top face differs from side face (bark texture on sides).
//        generator.registerLog(BuganairBlocks.SKYWOOD_LOG)
//                .log(BuganairBlocks.SKYWOOD_LOG)
//                .wood(BuganairBlocks.SKYWOOD_WOOD);
//        generator.registerLog(BuganairBlocks.STRIPPED_SKYWOOD_LOG)
//                .log(BuganairBlocks.STRIPPED_SKYWOOD_LOG)
//                .wood(BuganairBlocks.STRIPPED_SKYWOOD_WOOD);
//
//        // Simple cube-all blocks.
//        generator.registerSimpleCubeAll(BuganairBlocks.SKYWOOD_PLANKS);
//        generator.registerSimpleCubeAll(BuganairBlocks.SKYSTONE);
//        generator.registerSimpleCubeAll(BuganairBlocks.SKY_CRYSTAL_ORE);
//
//        // Slab + stairs (vanilla helpers handle the 3 blockstate variants
//        // for slabs, and the many rotation states for stairs).
//        generator.registerSlab(BuganairBlocks.SKYSTONE_SLAB, BuganairBlocks.SKYSTONE);
//        generator.registerStairs(BuganairBlocks.SKYSTONE_STAIRS);
//
//        // Leaves: standard "cutout" leaves model.
//        generator.registerSingleton(BuganairBlocks.SKYWOOD_LEAVES, TexturedModel.LEAVES);
//
//        // Skygrass: simple full cube for now. Swap to a top/side/bottom
//        // three-texture model later if you want a grass-block-style look.
//        generator.registerSimpleCubeAll(BuganairBlocks.SKYGRASS);
//
//        // Aether Forge: ideally directional (FACING) + lit/unlit (LIT), like
//        // a furnace. That needs hand-written VariantsBlockStateSupplier code
//        // rather than a one-line helper -- see vanilla's FURNACE/BLAST_FURNACE
//        // registration in BlockStateModelGenerator.registerBlockStates() for
//        // the pattern. Using a plain cube-all as a placeholder for now so
//        // datagen doesn't fail; replace once you wire up the real model.
//        generator.registerSimpleCubeAll(BuganairBlocks.AETHER_FORGE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        // BlockItems generally don't need explicit item models if their
        // block model is a simple cube/cross -- the block state generators
        // above already produce the item model as a side effect for most
        // of these. Nothing additional needed here unless you want custom
        // hand-held/GUI-only item models later.
    }
}
