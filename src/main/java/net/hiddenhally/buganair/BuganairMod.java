package net.hiddenhally.buganair;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hiddenhally.buganair.client.BuganairSpruceBoatModel;
import net.hiddenhally.buganair.config.BuganairConfig;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.hiddenhally.buganair.item.BuganairBoatItem;
import net.hiddenhally.buganair.network.*;
import net.hiddenhally.buganair.screen.BuganairBoatScreenHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
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
import net.hiddenhally.buganair.item.BuganairSniperItem;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Rarity;
import net.minecraft.util.Unit;
import net.minecraft.util.math.Vec3d;
import net.hiddenhally.buganair.item.BuganairHangGliderItem;
import net.hiddenhally.buganair.BuganairServerGliderState;
import net.minecraft.item.equipment.EquipmentAssetKeys; // Make sure to import this!

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.hiddenhally.buganair.Buganair.MOD_ID;

public class BuganairMod implements ModInitializer {
    //import net.minecraft.enchantment.Enchantment;
    // Add this near your other RegistryKeys
    public static final RegistryKey<Enchantment> WIND_RIDER = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "wind_rider"));
    public static final RegistryKey<Enchantment> AERODYNAMIC = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "aerodynamic"));
    public static final RegistryKey<Enchantment> THERMAL_LIFT = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "thermal_lift"));
    public static final RegistryKey<Enchantment> LIGHTWEIGHT = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(MOD_ID, "lightweight"));
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
    private static final RegistryKey<EntityType<?>> BUGANAIR_SCOUTING_FLARE_ENTITY_KEY = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "buganair_scouting_flare"));



    // Cooldown server-side, indipendente da eventuali API che cambiano tra versioni
    private static final Map<UUID, Long> SNIPER_LAST_FIRE_TICK = new HashMap<>();

    public static final Item BUGANAIR_SNIPER_ITEM = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "buganair_sniper"),
            new BuganairSniperItem(
                    new Item.Settings()
                            .maxCount(1)
                            // Sets the text color to Epic (Purple)
                            .component(DataComponentTypes.RARITY, Rarity.EPIC)
                            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "buganair_sniper")))
            )
    );

    // 1. A thread-safe Set to keep track of which players are currently crawling with the sniper
    private static final java.util.Set<UUID> SNIPER_CRAWLING_PLAYERS = java.util.concurrent.ConcurrentHashMap.newKeySet();

    // 2. The method your Mixin is trying to call
    public static boolean isSniperCrawling(UUID uuid) {
        return SNIPER_CRAWLING_PLAYERS.contains(uuid);
    }

    // 3. A helper method so you can easily add or remove players from this state
    public static void setSniperCrawling(UUID uuid, boolean isCrawling) {
        if (isCrawling) {
            SNIPER_CRAWLING_PLAYERS.add(uuid);
        } else {
            SNIPER_CRAWLING_PLAYERS.remove(uuid);
        }
    }



    public static final Item BUGANAIR_HANG_GLIDER_ITEM = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "buganair_hang_glider"),
            new BuganairHangGliderItem(
                    new Item.Settings()
                            .maxCount(1)
                            .maxDamage(432*2) // Defines the durability pool
                            .enchantable(100) // <-- ADD THIS LINE
                            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "buganair_hang_glider")))
                            // Sets the text color to Epic (Purple)
                            .component(DataComponentTypes.RARITY, Rarity.EPIC)

                            // 1. Enables flight & automatically grants the "don't break at 0 durability" protection
                            .component(DataComponentTypes.GLIDER, Unit.INSTANCE)

                            // 2. Defines what item fixes it in an anvil natively
                            .component(
                                    DataComponentTypes.REPAIRABLE,
                                    new RepairableComponent(RegistryEntryList.of(Items.PHANTOM_MEMBRANE.getRegistryEntry()))
                            )

                            // 3. Links your custom 3D model asset
                            .component(
                                    DataComponentTypes.EQUIPPABLE,
                                    EquippableComponent.builder(EquipmentSlot.CHEST)
                                            .model(RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(MOD_ID, "buganair_hang_glider")))
                                            .build()
                            )
            )
    );

    // 1. Add Item Registry
    public static final Item BUGANAIR_ORE_RADAR_ITEM = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "buganair_ore_radar"),
            new net.hiddenhally.buganair.item.BuganairOreRadarItem(
                    new Item.Settings().
                            maxCount(1)// Sets the text color to Epic (Purple)
                            .component(DataComponentTypes.RARITY, Rarity.EPIC)
                            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "buganair_ore_radar")))
            )
    );

    // ── Probabilità di spawn nel buried treasure (0.0f – 1.0f) ──────────────────
    public static final float RECIPE_MAP_LOOT_CHANCE = 0.05f; // 5%

    // ── Blueprint item ────────────────────────────────────────────────────────────
    public static final Item BUGANAIR_RECIPE_MAP_ITEM = Registry.register(
            Registries.ITEM,
            Identifier.of(MOD_ID, "buganair_recipe_map"),
            new net.hiddenhally.buganair.item.BuganairRecipeMapItem(
                    new Item.Settings()
                            .maxCount(1)
                            // Sets the text color to Epic (Purple)
                            .component(DataComponentTypes.RARITY, Rarity.EPIC)
                            .registryKey(RegistryKey.of(
                                    RegistryKeys.ITEM,
                                    Identifier.of(MOD_ID, "buganair_recipe_map")))
            )
    );

    // 1. Registrazione dell'Item del Bengala
    public static final Item BUGANAIR_SCOUTING_FLARE_ITEM = Registry.register(
            Registries.ITEM,
            net.minecraft.util.Identifier.of(MOD_ID, "buganair_scouting_flare"),
            new net.hiddenhally.buganair.item.BuganairScoutingFlareItem(
                    new Item.Settings()
                            .maxCount(16) // Stackabile fino a 16 unità
                            .component(DataComponentTypes.RARITY, Rarity.RARE)
                            .registryKey(RegistryKey.of(RegistryKeys.ITEM, net.minecraft.util.Identifier.of(MOD_ID, "buganair_scouting_flare")))
            )
    );

    // 2. Registrazione dell'Entità del Bengala
    public static final EntityType<net.hiddenhally.buganair.entity.BuganairScoutingFlareEntity> BUGANAIR_SCOUTING_FLARE_ENTITY_TYPE = Registry.register(
            Registries.ENTITY_TYPE,
            net.minecraft.util.Identifier.of(MOD_ID, "buganair_scouting_flare"),
            EntityType.Builder.<net.hiddenhally.buganair.entity.BuganairScoutingFlareEntity>create(net.hiddenhally.buganair.entity.BuganairScoutingFlareEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
                    .build(BUGANAIR_SCOUTING_FLARE_ENTITY_KEY)
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
                    .dimensions(2.625F, 1.0625F)
                    .maxTrackingRange(10)
                    .build(BUGANAIR_SPRUCE_BOAT_ENTITY_KEY)
    );

    // Create a quick helper method to register items easily
    private static Item registerBoatItem(String woodName) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "buganair_" + woodName + "_boat"));
        return Registry.register(
                Registries.ITEM,
                key,
                new BuganairBoatItem(woodName, new Item.Settings()
                        .maxCount(1)// Sets the text color to Epic (Purple)
                        .component(DataComponentTypes.RARITY, Rarity.RARE)
                        .registryKey(key))
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
                entries.add(BuganairMod.BUGANAIR_SNIPER_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_HANG_GLIDER_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_ORE_RADAR_ITEM.getDefaultStack());
                entries.add(BuganairMod.BUGANAIR_SCOUTING_FLARE_ITEM.getDefaultStack());
            })
            .build();

    @Override
    public void onInitialize() {

        // Run this inside your main onInitialize() method
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            var scoreboard = server.getScoreboard();
            if (scoreboard.getNullableObjective("scoping") == null) {
                scoreboard.addObjective(
                        "scoping",
                        ScoreboardCriterion.DUMMY,
                        Text.literal("Is Scoping"),
                        ScoreboardCriterion.RenderType.INTEGER,
                        true,
                        null
                );
            }
        });

        // Run this inside your main onInitialize() method
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            var scoreboard = server.getScoreboard();
            if (scoreboard.getNullableObjective("using_hang_glider") == null) {
                scoreboard.addObjective(
                        "using_hang_glider",
                        ScoreboardCriterion.DUMMY,
                        Text.literal("Is Using Hang Glider"),
                        ScoreboardCriterion.RenderType.INTEGER,
                        true,
                        null
                );
            }
        });

        // Run this inside your main onInitialize() method
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            var scoreboard = server.getScoreboard();
            if (scoreboard.getNullableObjective("using_ore_radar") == null) {
                scoreboard.addObjective(
                        "using_ore_radar",
                        ScoreboardCriterion.DUMMY,
                        Text.literal("Is Using Ore Radar"),
                        ScoreboardCriterion.RenderType.INTEGER,
                        true,
                        null
                );
            }
        });

        // Run this inside your main onInitialize() method
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            var scoreboard = server.getScoreboard();
            if (scoreboard.getNullableObjective("using_scouting_flare") == null) {
                scoreboard.addObjective(
                        "using_scouting_flare",
                        ScoreboardCriterion.DUMMY,
                        Text.literal("Is Using Scouting Flare"),
                        ScoreboardCriterion.RenderType.INTEGER,
                        true,
                        null
                );
            }
        });

        // 2. Load Config first
        net.hiddenhally.buganair.config.BuganairConfig.load();

        // 3. Register Payload
        PayloadTypeRegistry.playS2C().register(BuganairRadarSyncPayload.ID, BuganairRadarSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BuganairScoutingFlareSyncPayload.ID, BuganairScoutingFlareSyncPayload.CODEC);

        PayloadTypeRegistry.playC2S().register(BuganairBoatInputPayload.ID, BuganairBoatInputPayload.CODEC);

        // 1. REGISTER THE PAYLOADS
        // Add this alongside your existing glider payload registrations:
        PayloadTypeRegistry.playC2S().register(BuganairSniperCrawlPayload.ID, BuganairSniperCrawlPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(BuganairSniperFirePayload.ID, BuganairSniperFirePayload.CODEC);

        PayloadTypeRegistry.playC2S().register(BuganairSniperScopePayload.ID, BuganairSniperScopePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(BuganairGliderPayload.ID, BuganairGliderPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(BuganairOreRadarPayload.ID, BuganairOreRadarPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(BuganairScoutingFlarePayload.ID, BuganairScoutingFlarePayload.CODEC);

        PayloadTypeRegistry.playC2S().register(BuganairGliderTogglePayload.ID, BuganairGliderTogglePayload.CODEC);
        // Register the custom payload channel
        PayloadTypeRegistry.playC2S().register(BuganairGliderOrientationPayload.ID, BuganairGliderOrientationPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BuganairBoatInputPayload.ID, (payload, context) -> context.server().execute(() -> {
            ServerPlayerEntity player = context.player();
            if (player.getEntityWorld().getEntityById(payload.entityId()) instanceof BuganairBoatEntity boat && player.getVehicle() == boat) {
                boat.setMovementInput(payload.forward(), payload.sideways(), payload.vertical());
                boat.setSpeedSettings(payload.horizontalSpeed(), payload.verticalSpeed());
            }
        }));

        // 2. REGISTER THE GLOBAL RECEIVER
        // Add this alongside your ServerPlayNetworking.registerGlobalReceiver blocks:
        ServerPlayNetworking.registerGlobalReceiver(BuganairSniperCrawlPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                // Update the server-side state map based on the client's action
                setSniperCrawling(player.getUuid(), payload.crawling());
            });
        });

        // 3. PREVENT MEMORY LEAKS (Clean up when a player leaves the server)
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID playerUuid = handler.getPlayer().getUuid();
            setSniperCrawling(playerUuid, false);
        });

        ServerPlayNetworking.registerGlobalReceiver(BuganairSniperFirePayload.ID, (payload, context) -> context.server().execute(() -> {
            ServerPlayerEntity player = context.player();
            ServerWorld world = (ServerWorld) player.getEntityWorld();

            long now = world.getTime();
            long last = SNIPER_LAST_FIRE_TICK.getOrDefault(player.getUuid(), 0L);
            if (now - last < BuganairConfig.INSTANCE.SNIPER_FIRE_COOLDOWN_TICKS) {
                return; // ancora in cooldown, ignora il colpo
            }
            SNIPER_LAST_FIRE_TICK.put(player.getUuid(), now);

            // 1. Calculate the shifted origin using the payload's xOffset
            Vec3d centerEyePos = player.getEyePos();
            float yawRadians = (float) Math.toRadians(player.getYaw() + 90.0f);

            // Create the "Right Vector" based on the player's rotation
            Vec3d rightVector = new Vec3d(-Math.sin(yawRadians), 0, Math.cos(yawRadians));
            Vec3d peekOffsetVector = rightVector.multiply(payload.xOffset());

            // The exact 3D coordinate where the camera is located
            Vec3d spawnPos = centerEyePos.add(peekOffsetVector);

            Vec3d direction = player.getRotationVector();

            // 2. Use the exact coordinate constructor (x, y, z) instead of passing the PlayerEntity
            ArrowEntity arrow = new ArrowEntity(world, spawnPos.x, spawnPos.y, spawnPos.z, new ItemStack(Items.ARROW), new ItemStack(Items.BOW));

            // CRITICAL: Because we didn't pass 'player' to the constructor, we must manually set the owner!
            arrow.setOwner(player);

            arrow.setNoGravity(true);
            arrow.setVelocity(direction.multiply(BuganairConfig.INSTANCE.SNIPER_ARROW_SPEED));
            arrow.setDamage(BuganairConfig.INSTANCE.SNIPER_ARROW_DAMAGE);

            world.spawnEntity(arrow);

            // 3. Play the sound at the shifted spawn position so it sounds accurate in stereo!
            world.playSound(null, spawnPos.x, spawnPos.y, spawnPos.z,
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }));

        ServerPlayNetworking.registerGlobalReceiver(BuganairSniperScopePayload.ID, (payload, context) -> {
            // Ensure processing happens safely on the main server thread
            context.server().execute(() -> {
                var player = context.player();
                var server = context.server();
                Scoreboard scoreboard = server.getScoreboard();
                ScoreboardObjective objective = scoreboard.getNullableObjective("scoping");

                if (objective != null) {
                    // In modern versions, getOrCreateScore returns a ScoreAccess controller interface
                    ScoreAccess scoreAccess = scoreboard.getOrCreateScore(player, objective);

                    // Updates the integer value directly on the scoreboard channel
                    scoreAccess.setScore(payload.isAiming() ? 1 : 0);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(BuganairGliderPayload.ID, (payload, context) -> {
            // Ensure processing happens safely on the main server thread
            context.server().execute(() -> {
                var player = context.player();
                var server = context.server();
                Scoreboard scoreboard = server.getScoreboard();
                ScoreboardObjective objective = scoreboard.getNullableObjective("using_hang_glider");

                if (objective != null) {
                    // In modern versions, getOrCreateScore returns a ScoreAccess controller interface
                    ScoreAccess scoreAccess = scoreboard.getOrCreateScore(player, objective);

                    // Updates the integer value directly on the scoreboard channel
                    scoreAccess.setScore(payload.isGliding() ? 1 : 0);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(BuganairOreRadarPayload.ID, (payload, context) -> {
            // Ensure processing happens safely on the main server thread
            context.server().execute(() -> {
                var player = context.player();
                var server = context.server();
                Scoreboard scoreboard = server.getScoreboard();
                ScoreboardObjective objective = scoreboard.getNullableObjective("using_ore_radar");

                if (objective != null) {
                    // In modern versions, getOrCreateScore returns a ScoreAccess controller interface
                    ScoreAccess scoreAccess = scoreboard.getOrCreateScore(player, objective);

                    // Updates the integer value directly on the scoreboard channel
                    scoreAccess.setScore(payload.isSearching() ? 1 : 0);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(BuganairScoutingFlarePayload.ID, (payload, context) -> {
            // Ensure processing happens safely on the main server thread
            context.server().execute(() -> {
                var player = context.player();
                var server = context.server();
                Scoreboard scoreboard = server.getScoreboard();
                ScoreboardObjective objective = scoreboard.getNullableObjective("using_scouting_flare");

                if (objective != null) {
                    // In modern versions, getOrCreateScore returns a ScoreAccess controller interface
                    ScoreAccess scoreAccess = scoreboard.getOrCreateScore(player, objective);

                    // Updates the integer value directly on the scoreboard channel
                    scoreAccess.setScore(payload.isScouting() ? 1 : 0);
                }
            });
        });

        // Handle the packet when received from a client
        ServerPlayNetworking.registerGlobalReceiver(BuganairGliderTogglePayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            boolean wantsToGlide = payload.isGliding();

            if (wantsToGlide && BuganairHangGliderItem.isWearingGlider(player) && !player.isOnGround()) {
                BuganairServerGliderState.setGliding(player.getUuid(), true);
            } else {
                BuganairServerGliderState.setGliding(player.getUuid(), false);
            }
        });

        // Process arriving client flight updates on the logical server
        // Process arriving client flight updates safely on the logical server main thread
        ServerPlayNetworking.registerGlobalReceiver(BuganairGliderOrientationPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.player();
                BuganairServerGliderState.updateOrientation(player.getUuid(), payload.pitch(), payload.yaw(), payload.roll());
            });
        });

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
