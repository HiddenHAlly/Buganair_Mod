package net.hiddenhally.buganair.worldgen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.hiddenhally.buganair.block.BuganairBlocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

/**
 * Assigns mining-tool tags (axe/pickaxe) and a couple of vanilla behavioral
 * tags (logs-that-burn, leaves) to the new Buganair blocks.
 */
public class BuganairBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public BuganairBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getTagBuilder(BlockTags.AXE_MINEABLE)
                .add(BuganairBlocks.SKYWOOD_LOG.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.SKYWOOD_WOOD.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.STRIPPED_SKYWOOD_LOG.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.STRIPPED_SKYWOOD_WOOD.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.SKYWOOD_PLANKS.getRegistryEntry().registryKey().getRegistry());

        getTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(BuganairBlocks.SKYSTONE.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.SKYSTONE_SLAB.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.SKYSTONE_STAIRS.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.SKY_CRYSTAL_ORE.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.AETHER_FORGE.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.SKYGRASS.getRegistryEntry().registryKey().getRegistry());

        getTagBuilder(BlockTags.HOE_MINEABLE)
                .add(BuganairBlocks.SKYWOOD_LEAVES.getRegistryEntry().registryKey().getRegistry());

        getTagBuilder(BlockTags.LOGS_THAT_BURN)
                .add(BuganairBlocks.SKYWOOD_LOG.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.SKYWOOD_WOOD.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.STRIPPED_SKYWOOD_LOG.getRegistryEntry().registryKey().getRegistry())
                .add(BuganairBlocks.STRIPPED_SKYWOOD_WOOD.getRegistryEntry().registryKey().getRegistry());

        getTagBuilder(BlockTags.LEAVES)
                .add(BuganairBlocks.SKYWOOD_LEAVES.getRegistryEntry().registryKey().getRegistry());
    }
}
