package net.hiddenhally.buganair.worldgen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.Biome;

import java.util.concurrent.CompletableFuture;

/**
 * Tags the Sky Islands biome for correct vanilla biome-tag behavior (music,
 * ambient sound, certain spawn rules that key off tags like is_overworld).
 *
 * IMPORTANT: this tag alone does NOT make the biome get chosen during
 * terrain generation. Since 1.18, Overworld biome selection runs off a
 * MultiNoiseBiomeSourceParameterList keyed on climate parameters, not tags.
 * See net.hiddenhally.buganair.worldgen.BuganairWorldgenInit for the runtime
 * wiring (BiomeModifications.addFeature) that actually makes sky islands
 * appear in generated worlds by injecting BuganairFeatures.SKY_ISLAND_PLACED
 * into existing overworld biomes, since this mod's islands are placed via a
 * directly-invoked Feature rather than relying on SKY_ISLANDS being
 * "selected" as the region's biome.
 */
public class BuganairBiomeTagProvider extends FabricTagProvider<Biome> {

    public BuganairBiomeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BIOME, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getTagBuilder(BiomeTags.IS_OVERWORLD)
                .add(BuganairBiomes.SKY_ISLANDS.getRegistry());
    }
}
