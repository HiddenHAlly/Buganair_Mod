package net.hiddenhally.buganair;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.hiddenhally.buganair.item.BuganairBoatItem;
import net.hiddenhally.buganair.network.BuganairBoatInputPayload;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BuganairMod implements ModInitializer {
    private static final RegistryKey<EntityType<?>> BUGANAIR_BOAT_ENTITY_KEY = RegistryKey.of(
        RegistryKeys.ENTITY_TYPE,
        Identifier.of(Buganair.MOD_ID, "buganair_boat")
    );
    private static final RegistryKey<Item> BUGANAIR_BOAT_ITEM_KEY = RegistryKey.of(
        RegistryKeys.ITEM,
        Identifier.of(Buganair.MOD_ID, "buganair_boat")
    );

    public static final EntityType<BuganairBoatEntity> BUGANAIR_BOAT_ENTITY_TYPE = Registry.register(
        Registries.ENTITY_TYPE,
        BUGANAIR_BOAT_ENTITY_KEY,
        EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC)
            .dimensions(1.375F, 0.5625F)
            .maxTrackingRange(10)
            .trackingTickInterval(3)
            .build(BUGANAIR_BOAT_ENTITY_KEY)
    );

    public static final Item BUGANAIR_BOAT_ITEM = Registry.register(
        Registries.ITEM,
        BUGANAIR_BOAT_ITEM_KEY,
        new BuganairBoatItem(new Item.Settings().maxCount(1).registryKey(BUGANAIR_BOAT_ITEM_KEY))
    );

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(BuganairBoatInputPayload.ID, BuganairBoatInputPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BuganairBoatInputPayload.ID, (payload, context) -> context.server().execute(() -> {
            ServerPlayerEntity player = context.player();
            if (player.getEntityWorld().getEntityById(payload.entityId()) instanceof BuganairBoatEntity boat && player.getVehicle() == boat) {
                boat.setMovementInput(payload.forward(), payload.sideways(), payload.vertical());
                boat.setSpeedSettings(payload.horizontalSpeed(), payload.verticalSpeed());
            }
        }));

        Buganair.LOGGER.info("Mod {} initialized", Buganair.MOD_ID);
    }
}
