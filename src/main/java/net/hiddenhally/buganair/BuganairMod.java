package net.hiddenhally.buganair;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.hiddenhally.buganair.item.BuganairBoatItem;
import net.hiddenhally.buganair.network.BuganairBoatInputPayload;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BuganairMod implements ModInitializer {
    public static final EntityType<BuganairBoatEntity> BUGANAIR_BOAT_ENTITY_TYPE = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of(Buganair.MOD_ID, "buganair_boat"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, BuganairBoatEntity::new)
            .dimensions(EntityDimensions.fixed(1.375F, 0.5625F))
            .trackRangeBlocks(10)
            .trackedUpdateRate(3)
            .build()
    );

    public static final Item BUGANAIR_BOAT_ITEM = Registry.register(
        Registries.ITEM,
        Identifier.of(Buganair.MOD_ID, "buganair_boat"),
        new BuganairBoatItem(new Item.Settings().maxCount(1))
    );

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(BuganairBoatInputPayload.ID, BuganairBoatInputPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BuganairBoatInputPayload.ID, (payload, context) -> context.server().execute(() -> {
            ServerPlayerEntity player = context.player();
            if (player.getWorld().getEntityById(payload.entityId()) instanceof BuganairBoatEntity boat && player.getVehicle() == boat) {
                boat.setMovementInput(payload.forward(), payload.sideways(), payload.vertical());
            }
        }));

        Buganair.LOGGER.info("Mod {} initialized", Buganair.MOD_ID);
    }
}
