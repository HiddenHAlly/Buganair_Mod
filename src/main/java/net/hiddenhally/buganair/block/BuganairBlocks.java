package net.hiddenhally.buganair.block;

import com.mojang.serialization.MapCodec;
import net.hiddenhally.buganair.Buganair;
import net.hiddenhally.buganair.block.AetherForgeBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static net.hiddenhally.buganair.Buganair.MOD_ID;

/**
 * Registers all custom blocks for the Buganair mod.
 *
 * Theme: "Skywood" — a pale, glowing wood type found on floating sky islands.
 *
 * Block categories:
 *   - Decorative : skywood_log, skywood_wood, stripped_skywood_log,
 *                  stripped_skywood_wood, skywood_planks, skywood_leaves,
 *                  skywood_sapling
 *   - Ore        : sky_crystal_ore (spawns in sky islands)
 *   - Functional : aether_forge (custom crafting block)
 *   - Structure  : skystone (island foundation block)
 *
 * Each block is also registered as a BlockItem so it is obtainable in-game.
 */
public class BuganairBlocks {

    // ── Decorative / Wood ──────────────────────────────────────────────────────

    /** Unstripped Skywood log. Behaves like a vanilla log. */
    public static final Block SKYWOOD_LOG = register(
            "skywood_log",
            new PillarBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.OFF_WHITE)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)
                    .sounds(BlockSoundGroup.WOOD)
                    .burnable()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "skywood_log")))
            )
    );

    /** Skywood "wood" (log with bark on all sides). */
    public static final Block SKYWOOD_WOOD = register(
            "skywood_wood",
            new PillarBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.OFF_WHITE)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)
                    .sounds(BlockSoundGroup.WOOD)
                    .burnable()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "skywood_wood")))
            )
    );

    /** Stripped skywood log. */
    public static final Block STRIPPED_SKYWOOD_LOG = register(
            "stripped_skywood_log",
            new PillarBlock(AbstractBlock.Settings.copy(SKYWOOD_LOG)
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "stripped_skywood_log"))))
    );

    /** Stripped skywood wood. */
    public static final Block STRIPPED_SKYWOOD_WOOD = register(
            "stripped_skywood_wood",
            new PillarBlock(AbstractBlock.Settings.copy(SKYWOOD_WOOD)
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "stripped_skywood_wood"))))
    );

    /** Skywood planks — the processed building block. */
    public static final Block SKYWOOD_PLANKS = register(
            "skywood_planks",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.OFF_WHITE)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 3.0F)
                    .sounds(BlockSoundGroup.WOOD)
                    .burnable()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "skywood_planks")))
            )
    );

    /**
     * Skywood leaves — slightly luminous (light level 4) to give the biome
     * a soft glow at night.
     */
    public static final Block SKYWOOD_LEAVES = register(
            "skywood_leaves",
            new LeavesBlock(0.1f, AbstractBlock.Settings.create()
                    .mapColor(MapColor.PALE_PURPLE)
                    .strength(0.2F)
                    .ticksRandomly()
                    .sounds(BlockSoundGroup.GRASS)
                    .nonOpaque()
                    .allowsSpawning(Blocks::canSpawnOnLeaves)
                    .suffocates(Blocks::never)
                    .blockVision(Blocks::never)
                    .burnable()
                    .pistonBehavior(PistonBehavior.DESTROY)
                    .solidBlock(Blocks::never)
                    .luminance(state -> 4)         // soft glow
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "skywood_leaves")))
            ) {
                @Override
                public MapCodec<? extends LeavesBlock> getCodec() {
                    return null;
                }

                @Override
                protected void spawnLeafParticle(World world, BlockPos pos, Random random) {

                }
            }
    );

    // ── Ore ────────────────────────────────────────────────────────────────────

    /**
     * Sky Crystal Ore — the unique ore of sky islands.
     * Drops 1-3 sky crystals when mined (handled via loot table).
     * Emits a subtle light (level 5) so it can be spotted mid-flight.
     */
    public static final Block SKY_CRYSTAL_ORE = register(
            "sky_crystal_ore",
            new ExperienceDroppingBlock(
                    ConstantIntProvider.create(0), // XP handled by loot table
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.STONE_GRAY)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .requiresTool()
                            .strength(4.5F, 3.0F)
                            .sounds(BlockSoundGroup.STONE)
                            .luminance(state -> 5)
                            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "sky_crystal_ore")))
            )
    );

    // ── Functional ─────────────────────────────────────────────────────────────

    /**
     * Aether Forge — a custom 3×3 crafting station unique to this mod.
     * For now it extends {@link AbstractFurnaceBlock}-style settings;
     * the actual GUI/screen handler can be wired up separately.
     *
     * Note: if you don't yet have an AetherForgeBlock class, this falls back
     * to a plain Block placeholder so the registration compiles.  Replace the
     * class once you implement the screen handler.
     */
    public static final Block AETHER_FORGE = register(
            "aether_forge",
            new AetherForgeBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.STONE_GRAY)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresTool()
                    .strength(3.5F, 6.0F)
                    .sounds(BlockSoundGroup.STONE)
                    .luminance(state -> 7)          // glows when active
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "aether_forge")))
            )
    );

    // ── Structure / Foundation ─────────────────────────────────────────────────

    /**
     * Skystone — the pale stone that makes up the body of sky islands.
     * Slightly softer than regular stone so islands feel distinct.
     */
    public static final Block SKYSTONE = register(
            "skystone",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.OFF_WHITE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresTool()
                    .strength(1.5F, 6.0F)
                    .sounds(BlockSoundGroup.STONE)
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "skystone")))
            )
    );

    public static final Block SKYGRASS = register(
            "skygrass",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.OFF_WHITE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresTool()
                    .strength(1.5F, 6.0F)
                    .sounds(BlockSoundGroup.STONE)
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "skygrass")))
            )
    );

    /** Skystone slab. */
    public static final Block SKYSTONE_SLAB = register(
            "skystone_slab",
            new SlabBlock(AbstractBlock.Settings.copy(SKYSTONE)
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "skystone_slab"))))
    );

    /** Skystone stairs. */
    public static final Block SKYSTONE_STAIRS = register(
            "skystone_stairs",
            new StairsBlock(SKYSTONE.getDefaultState(), AbstractBlock.Settings.copy(SKYSTONE)
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, net.minecraft.util.Identifier.of(MOD_ID, "skystone_stairs"))))
    );

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Registers a block AND its corresponding BlockItem in one call.
     *
     * @param name  registry path (no namespace prefix)
     * @param block the block instance to register
     * @return the same block instance (for static field assignment)
     */
    private static <T extends Block> T register(String name, T block) {
        Identifier id = Identifier.of(MOD_ID, name);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        RegistryKey<Item> itemKey   = RegistryKey.of(RegistryKeys.ITEM, id);

        // Register the block
        Registry.register(Registries.BLOCK, id, block);

        // Register the BlockItem so the block is obtainable in survival
        Registry.register(
                Registries.ITEM,
                id,
                new BlockItem(block, new Item.Settings().registryKey(itemKey))
        );

        Buganair.LOGGER.debug("[Buganair] Registered block + item: {}", id);
        return block;
    }

    /**
     * Call this method from {@link net.hiddenhally.buganair.BuganairMod#onInitialize()}
     * to trigger static class loading and perform all registrations.
     *
     * Example:
     * <pre>{@code
     *   BuganairBlocks.register();
     * }</pre>
     */
    public static void register() {
        Buganair.LOGGER.info("[Buganair] Registering blocks...");
        // Static initializers run on first class access — this method
        // just ensures the class is loaded at the right time.
    }
}