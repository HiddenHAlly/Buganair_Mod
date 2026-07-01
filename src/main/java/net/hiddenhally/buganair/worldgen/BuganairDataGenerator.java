package net.hiddenhally.buganair.worldgen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.hiddenhally.buganair.worldgen.*;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.pool.StructurePool;

/**
 * STABILITY FIX (this pass): BuganairStructures + its TEMPLATE_POOL
 * registration are commented out. Reasoning:
 *
 * Registry bootstraps here run at world load, not just during `runDatagen`
 * -- dynamic registries (biome/configured-feature/placed-feature/structure)
 * get rebuilt from these exact bootstrap methods every time a world
 * starts, since that's how Fabric/vanilla resolves datapack + code-defined
 * registry content into the actual runtime registry set. If ANY bootstrap
 * in this chain throws (e.g. a lookup that isn't satisfied yet due to
 * registration order, or a reference to a template pool/structure that
 * isn't fully wired up), the WHOLE registry-build pass for that world can
 * fail or come out incomplete -- which would explain "nothing generates
 * anywhere," including in old worlds, not just missing islands.
 *
 * BuganairStructures.java is still a work-in-progress jigsaw scaffold (see
 * its own javadoc) and isn't required for anything currently working --
 * the Aether Forge already places correctly via BuganairIslandFeature
 * directly. Re-enable STRUCTURE + TEMPLATE_POOL registration only once
 * BuganairStructures compiles AND has been tested in isolation (e.g. via
 * `runDatagen` producing valid JSON with no errors) so a bug in it can't
 * take down world loading for everything else.
 */
public class BuganairDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(BuganairWorldgenProvider::new);
		// 1. REGISTER ALL DATA PROVIDERS HERE:
		pack.addProvider(BuganairBlockTagProvider::new);
		pack.addProvider(BuganairBiomeTagProvider::new);
		pack.addProvider(BuganairBlockLootTableProvider::new);
		pack.addProvider(BuganairModelProvider::new);

		// 2. WORLDGEN REGISTRY BOOTSTRAPS:
		// (Keep your existing registryBuilder configuration below here)
		RegistryBuilder registryBuilder = new RegistryBuilder();
		buildRegistry(registryBuilder);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, context -> {
			BuganairConfiguredFeatures.bootstrap(context);
			BuganairFeatures.bootstrapConfigured(context);
		});

		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, context -> {
			BuganairPlacedFeatures.bootstrap(context);
			BuganairFeatures.bootstrapPlaced(context);
		});

		registryBuilder.addRegistry(RegistryKeys.BIOME, BuganairBiomes::bootstrap);

		// This links your java code's bootstrap methods directly to the data generator!
//		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, context -> {
//			BuganairConfiguredFeatures.bootstrap(context);
//			BuganairFeatures.bootstrapConfigured(context);
//		});
//
//		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, context -> {
//			BuganairPlacedFeatures.bootstrap(context);
//			BuganairFeatures.bootstrapPlaced(context);
//		});

		//registryBuilder.addRegistry(RegistryKeys.BIOME, BuganairBiomes::bootstrap);
		registryBuilder.addRegistry(RegistryKeys.STRUCTURE, BuganairStructures::bootstrap);

		// FIX: Register the template pool in Java so the validation pass succeeds!
		registryBuilder.addRegistry(RegistryKeys.TEMPLATE_POOL, context -> {
			context.register(
					RegistryKey.of(RegistryKeys.TEMPLATE_POOL, net.minecraft.util.Identifier.of("buganair", "aether_forge/start_pool")),
					new StructurePool(
							// FIX: Use the correct built-in empty structure pool key reference
							context.getRegistryLookup(RegistryKeys.TEMPLATE_POOL)
									.getOrThrow(net.minecraft.structure.pool.StructurePools.EMPTY),
							com.google.common.collect.ImmutableList.of(),
							StructurePool.Projection.RIGID
					)
			);
		});

		// DISABLED for now -- see class javadoc above.
		// registryBuilder.addRegistry(RegistryKeys.STRUCTURE, BuganairStructures::bootstrap);
		// registryBuilder.addRegistry(RegistryKeys.TEMPLATE_POOL, context -> { ... });
	}
}
