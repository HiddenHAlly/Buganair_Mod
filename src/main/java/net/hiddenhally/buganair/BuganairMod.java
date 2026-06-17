package net.hiddenhally.buganair;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hiddenhally.buganair.client.BuganairSpruceBoatModel;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.hiddenhally.buganair.item.BuganairBoatItem;
import net.hiddenhally.buganair.network.BuganairBoatInputPayload;
import net.hiddenhally.buganair.screen.BuganairBoatScreenHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

import static net.hiddenhally.buganair.Buganair.MOD_ID;

public class BuganairMod implements ModInitializer {
    private static final RegistryKey<EntityType<?>> BUGANAIR_ACACIA_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "buganair_acacia_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_BAMBOO_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "buganair_bamboo_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_BIRCH_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "buganair_birch_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_CHERRY_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "buganair_cherry_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_DARK_OAK_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "buganair_dark_oak_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_JUNGLE_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "buganair_jungle_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_MANGROVE_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "buganair_mangrove_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_OAK_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "buganair_oak_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_PALE_OAK_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "buganair_pale_oak_boat"));
    private static final RegistryKey<EntityType<?>> BUGANAIR_SPRUCE_BOAT_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "buganair_spruce_boat"));

    // ── Probabilità di spawn nel buried treasure (0.0f – 1.0f) ──────────────────
    public static final float RECIPE_MAP_LOOT_CHANCE = 0.05f; // 5%

    // ── Blueprint item ────────────────────────────────────────────────────────────
    public static final Item BUGANAIR_RECIPE_MAP_ITEM = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "buganair_recipe_map"),
            new net.hiddenhally.buganair.item.BuganairRecipeMapItem(
                    new Item.Settings()
                            .maxCount(1)
                            .registryKey(RegistryKey.of(
                                    RegistryKeys.ITEM,
                                    Identifier.of(MOD_ID, "buganair_recipe_map")))
            )
    );


    // Register your custom ScreenHandlerType directly here!
    public static final ScreenHandlerType<BuganairBoatScreenHandler> BUGANAIR_BOAT_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    Identifier.of(MOD_ID, "buganair_boat"),
                    new ScreenHandlerType<>(BuganairBoatScreenHandler::new, FeatureFlags.VANILLA_FEATURES)
            );

    private static final RegistryKey<Item> BUGANAIR_BOAT_ITEM_KEY = RegistryKey.of(
        RegistryKeys.ITEM,
        Identifier.of(MOD_ID, "buganair_boat")
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
    //public static final EntityType<BuganairBoatEntity> BUGANAIR_SPRUCE_BOAT_ENTITY_TYPE = Registry.register(Registries.ENTITY_TYPE, BUGANAIR_SPRUCE_BOAT_ENTITY_KEY, EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC).dimensions(1.375F, 0.5625F).maxTrackingRange(10).trackingTickInterval(3).build(BUGANAIR_SPRUCE_BOAT_ENTITY_KEY));
    public static final EntityType<BuganairBoatEntity> BUGANAIR_SPRUCE_BOAT_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            BUGANAIR_SPRUCE_BOAT_ENTITY_KEY,
            EntityType.Builder.create(BuganairBoatEntity::new, SpawnGroup.MISC)
                    // Vanilla boat is (1.375F, 0.5625F).
                    // Put your custom Spruce dimensions right here!
                    .dimensions(2.375F, 1.0625F)
                    .maxTrackingRange(10)
                    .build(BUGANAIR_SPRUCE_BOAT_ENTITY_KEY)
    );

    // Create a quick helper method to register items easily
    private static Item registerBoatItem(String woodName) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "buganair_" + woodName + "_boat"));
        return Registry.register(
                Registries.ITEM,
                key,
                new BuganairBoatItem(woodName, new Item.Settings().maxCount(1).registryKey(key))
        );
    }

    // All variants registered with string keys perfectly matching your asset paths
    public static final Item BUGANAIR_OAK_BOAT_ITEM = registerBoatItem("oak");
    public static final Item BUGANAIR_PALE_OAK_BOAT_ITEM = registerBoatItem("pale_oak");
    public static final Item BUGANAIR_SPRUCE_BOAT_ITEM = registerBoatItem("spruce");
    public static final Item BUGANAIR_BIRCH_BOAT_ITEM = registerBoatItem("birch");
    public static final Item BUGANAIR_JUNGLE_BOAT_ITEM = registerBoatItem("jungle");
    public static final Item BUGANAIR_ACACIA_BOAT_ITEM = registerBoatItem("acacia");
    public static final Item BUGANAIR_DARK_OAK_BOAT_ITEM = registerBoatItem("dark_oak");
    public static final Item BUGANAIR_MANGROVE_BOAT_ITEM = registerBoatItem("mangrove");
    public static final Item BUGANAIR_CHERRY_BOAT_ITEM = registerBoatItem("cherry");
    public static final Item BUGANAIR_BAMBOO_RAFT_ITEM = registerBoatItem("bamboo");

    // 1. Create the Registry Key for your custom tab
    public static final RegistryKey<ItemGroup> BUGANAIR_ITEM_GROUP_KEY = RegistryKey.of(
            RegistryKeys.ITEM_GROUP,
            Identifier.of(MOD_ID, "buganair_group")
    );

    // 2. Build the ItemGroup instance
    public static final ItemGroup BUGANAIR_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(BuganairMod.BUGANAIR_OAK_BOAT_ITEM))
            // 1. FIXED: Changed .title() to .displayName()
            .displayName(Text.translatable("itemGroup.buganair.buganair_group"))
            .entries((displayContext, entries) -> {
                // 2. FIXED: Appended .getDefaultStack() to turn the raw Items into ItemStacks
                entries.add(BuganairMod.BUGANAIR_ACACIA_BOAT_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_BAMBOO_RAFT_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_BIRCH_BOAT_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_CHERRY_BOAT_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_DARK_OAK_BOAT_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_JUNGLE_BOAT_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_MANGROVE_BOAT_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_OAK_BOAT_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_PALE_OAK_BOAT_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_SPRUCE_BOAT_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_RECIPE_MAP_ITEM.getDefaultStack());
            })
            .build();

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

        // 4. Register the group inside your onInitialize method
        Registry.register(Registries.ITEM_GROUP, BUGANAIR_ITEM_GROUP_KEY, BUGANAIR_ITEM_GROUP);

        // Nuovi import necessari:
        // import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
        // import net.minecraft.loot.LootPool;
        // import net.minecraft.loot.LootTables;
        // import net.minecraft.loot.condition.RandomChanceLootCondition;
        // import net.minecraft.loot.entry.ItemEntry;
        // import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

        LootTableEvents.MODIFY.register((registryKey, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) return; // non toccare i datapacks di altri mod

            if (LootTables.BURIED_TREASURE_CHEST.equals(registryKey)) {
                tableBuilder.pool(
                        LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .with(ItemEntry.builder(BUGANAIR_RECIPE_MAP_ITEM).weight(1))
                                .conditionally(RandomChanceLootCondition.builder(RECIPE_MAP_LOOT_CHANCE))
                );
                Buganair.LOGGER.info("[Buganair] Blueprint injected into buried_treasure ({} chance)",
                        RECIPE_MAP_LOOT_CHANCE);
            }
        });


        Buganair.LOGGER.info("Mod {} initialized", MOD_ID);
    }
}
