package net.hiddenhally.buganair;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.NonNull;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.hiddenhally.buganair.worldgen.*;

public class BuganairDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		// Add your dynamic worldgen provider to the datagen execution pipeline
		pack.addProvider(BuganairWorldgenProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		// This links your java code's bootstrap methods directly to the data generator!
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, context -> {
			BuganairConfiguredFeatures.bootstrap(context);
			BuganairFeatures.bootstrapConfigured(context);
		});

		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, context -> {
			BuganairPlacedFeatures.bootstrap(context);
			BuganairFeatures.bootstrapPlaced(context);
		});

		registryBuilder.addRegistry(RegistryKeys.BIOME, BuganairBiomes::bootstrap);
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
	}
}