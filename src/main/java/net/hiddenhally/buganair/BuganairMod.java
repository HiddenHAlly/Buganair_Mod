package net.hiddenhally.buganair;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hiddenhally.buganair.Buganair;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.hiddenhally.buganair.item.BuganairBoatItem;
import net.hiddenhally.buganair.network.BuganairBoatInputPayload;
import net.hiddenhally.buganair.screen.BuganairBoatScreenHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class BuganairMod implements ModInitializer {
    private static final RegistryKey<EntityType<?>> BUGANAIR_ACACIA_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Buganair.MOD_ID, "buganair_acacia_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_BAMBOO_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Buganair.MOD_ID, "buganair_bamboo_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_BIRCH_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Buganair.MOD_ID, "buganair_birch_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_CHERRY_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Buganair.MOD_ID, "buganair_cherry_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_DARK_OAK_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Buganair.MOD_ID, "buganair_dark_oak_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_JUNGLE_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Buganair.MOD_ID, "buganair_jungle_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_MANGROVE_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Buganair.MOD_ID, "buganair_mangrove_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_OAK_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Buganair.MOD_ID, "buganair_oak_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_PALE_OAK_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Buganair.MOD_ID, "buganair_pale_oak_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_SPRUCE_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Buganair.MOD_ID, "buganair_spruce_boat"));

    // Register your custom ScreenHandlerType directly here!
    public static final ScreenHandlerType<BuganairBoatScreenHandler> BUGANAIR_BOAT_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(Buganair.MOD_ID, "buganair_boat"),
                    new ScreenHandlerType<>(BuganairBoatScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
            );

    private static final RegistryKey<Item> BUGANAIR_BOAT_ITEM_KEY = RegistryKey.of(
        RegistryKeys.ITEM,
        Identifier.of(Buganair.MOD_ID, "buganair_boat")
    );

    public static final EntityType<BuganairBoatEntity> BUGANAIR_ACACIA_BOAT_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, BUGANAIR_ACACIA_BOAT_ENTITY_KEY, EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC).dimensions(1.375F, 0.5625F).maxTrackingRange(10).trackingTickInterval(3).build(BUGANAIR_ACACIA_BOAT_ENTITY_KEY));
    public static final EntityType<BuganairBoatEntity> BUGANAIR_BAMBOO_BOAT_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, BUGANAIR_BAMBOO_BOAT_ENTITY_KEY, EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC).dimensions(1.375F, 0.5625F).maxTrackingRange(10).trackingTickInterval(3).build(BUGANAIR_BAMBOO_BOAT_ENTITY_KEY));
    public static final EntityType<BuganairBoatEntity> BUGANAIR_BIRCH_BOAT_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, BUGANAIR_BIRCH_BOAT_ENTITY_KEY, EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC).dimensions(1.375F, 0.5625F).maxTrackingRange(10).trackingTickInterval(3).build(BUGANAIR_BIRCH_BOAT_ENTITY_KEY));
    public static final EntityType<BuganairBoatEntity> BUGANAIR_CHERRY_BOAT_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, BUGANAIR_CHERRY_BOAT_ENTITY_KEY, EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC).dimensions(1.375F, 0.5625F).maxTrackingRange(10).trackingTickInterval(3).build(BUGANAIR_CHERRY_BOAT_ENTITY_KEY));
    public static final EntityType<BuganairBoatEntity> BUGANAIR_DARK_OAK_BOAT_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, BUGANAIR_DARK_OAK_BOAT_ENTITY_KEY, EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC).dimensions(1.375F, 0.5625F).maxTrackingRange(10).trackingTickInterval(3).build(BUGANAIR_DARK_OAK_BOAT_ENTITY_KEY));
    public static final EntityType<BuganairBoatEntity> BUGANAIR_JUNGLE_BOAT_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, BUGANAIR_JUNGLE_BOAT_ENTITY_KEY, EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC).dimensions(1.375F, 0.5625F).maxTrackingRange(10).trackingTickInterval(3).build(BUGANAIR_JUNGLE_BOAT_ENTITY_KEY));
    public static final EntityType<BuganairBoatEntity> BUGANAIR_MANGROVE_BOAT_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, BUGANAIR_MANGROVE_BOAT_ENTITY_KEY, EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC).dimensions(1.375F, 0.5625F).maxTrackingRange(10).trackingTickInterval(3).build(BUGANAIR_MANGROVE_BOAT_ENTITY_KEY));
    public static final EntityType<BuganairBoatEntity> BUGANAIR_OAK_BOAT_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, BUGANAIR_OAK_BOAT_ENTITY_KEY, EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC).dimensions(1.375F, 0.5625F).maxTrackingRange(10).trackingTickInterval(3).build(BUGANAIR_OAK_BOAT_ENTITY_KEY));
    public static final EntityType<BuganairBoatEntity> BUGANAIR_PALE_OAK_BOAT_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, BUGANAIR_PALE_OAK_BOAT_ENTITY_KEY, EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC).dimensions(1.375F, 0.5625F).maxTrackingRange(10).trackingTickInterval(3).build(BUGANAIR_PALE_OAK_BOAT_ENTITY_KEY));
    public static final EntityType<BuganairBoatEntity> BUGANAIR_SPRUCE_BOAT_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, BUGANAIR_SPRUCE_BOAT_ENTITY_KEY, EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC).dimensions(1.375F, 0.5625F).maxTrackingRange(10).trackingTickInterval(3).build(BUGANAIR_SPRUCE_BOAT_ENTITY_KEY));


    // Create a quick helper method to register items easily
    private static Item registerBoatItem(String woodName) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Buganair.MOD_ID, "buganair_" + woodName + "_boat"));
        return Registry.register(
                Registries.ITEM,
                key,
                new BuganairBoatItem(woodName, new Item.Settings().maxCount(1).registryKey(key))
        );
    }

    // All variants registered with string keys perfectly matching your asset paths
    public static final Item BUGANAIR_OAK_BOAT = registerBoatItem("oak");
    public static final Item BUGANAIR_PALE_OAK_BOAT = registerBoatItem("pale_oak");
    public static final Item BUGANAIR_SPRUCE_BOAT = registerBoatItem("spruce");
    public static final Item BUGANAIR_BIRCH_BOAT = registerBoatItem("birch");
    public static final Item BUGANAIR_JUNGLE_BOAT = registerBoatItem("jungle");
    public static final Item BUGANAIR_ACACIA_BOAT = registerBoatItem("acacia");
    public static final Item BUGANAIR_DARK_OAK_BOAT = registerBoatItem("dark_oak");
    public static final Item BUGANAIR_MANGROVE_BOAT = registerBoatItem("mangrove");
    public static final Item BUGANAIR_CHERRY_BOAT = registerBoatItem("cherry");
    public static final Item BUGANAIR_BAMBOO_RAFT = registerBoatItem("bamboo");

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
