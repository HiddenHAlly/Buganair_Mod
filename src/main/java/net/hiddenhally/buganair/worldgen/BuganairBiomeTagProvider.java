package net.hiddenhally.buganair.worldgen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.Biome;

import java.util.concurrent.CompletableFuture;

/**
 * Tags the Sky Islands biome for correct vanilla biome-tag behavior.
 *
 * API NOTE: 1.21.11 uses getTagBuilder(...), not getOrCreateTagBuilder(...).
 * .add(RegistryKey<Biome>) takes the RegistryKey directly.
 */
public class BuganairBiomeTagProvider extends FabricTagProvider<Biome> {

    public BuganairBiomeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BIOME, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getTagBuilder(BiomeTags.IS_OVERWORLD)
                .add(BuganairBiomes.SKY_ISLANDS.getValue());
    }
}
