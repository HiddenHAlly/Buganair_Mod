package net.hiddenhally.buganair.worldgen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/**
 * Dumps all dynamically-registered worldgen objects (ConfiguredFeature,
 * PlacedFeature, Biome) to JSON under
 * src/main/generated/data/buganair/worldgen/...
 *
 * Run via the `runDatagen` Gradle task, then copy the generated files from
 * src/main/generated into src/main/resources (or configure your build to
 * do so automatically — check your existing datagen setup for the pattern
 * you're already using, since this mirrors BuganairDataGenerator's existing
 * conventions).
 */
public class BuganairWorldgenProvider extends FabricDynamicRegistryProvider {

    public BuganairWorldgenProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        entries.addAll(registries.getOrThrow(RegistryKeys.CONFIGURED_FEATURE));
        entries.addAll(registries.getOrThrow(RegistryKeys.PLACED_FEATURE));
        entries.addAll(registries.getOrThrow(RegistryKeys.BIOME));
        // Uncomment once BuganairStructures compiles cleanly against your
        // mappings and its structure-pool JSON exists:
        // entries.addAll(registries.getOrThrow(RegistryKeys.STRUCTURE));
    }

    @Override
    public String getName() {
        return "Buganair Worldgen (Configured/Placed Features, Biomes)";
    }
}
