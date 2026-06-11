package net.hiddenhally.buganair.item;

import net.hiddenhally.buganair.BuganairMod;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class BuganairBoatItem extends Item {
    private final String variant;
    // 1. Define the Map mapping your string variants to their EntityTypes
    private final Map<String, EntityType<BuganairBoatEntity>> entityTypes = new HashMap<>();

    public BuganairBoatItem(String variant, Settings settings) {
        super(settings);
        this.variant = variant;

        // 2. Populate the Map inside the constructor
        // Make sure these match the exact registry names or variant strings you pass in!
        this.entityTypes.put("oak", BuganairMod.BUGANAIR_OAK_BOAT_ENTITY_TYPE);
        this.entityTypes.put("birch", BuganairMod.BUGANAIR_BIRCH_BOAT_ENTITY_TYPE);
        this.entityTypes.put("spruce", BuganairMod.BUGANAIR_SPRUCE_BOAT_ENTITY_TYPE);
        this.entityTypes.put("jungle", BuganairMod.BUGANAIR_JUNGLE_BOAT_ENTITY_TYPE);
        this.entityTypes.put("acacia", BuganairMod.BUGANAIR_ACACIA_BOAT_ENTITY_TYPE);
        this.entityTypes.put("dark_oak", BuganairMod.BUGANAIR_DARK_OAK_BOAT_ENTITY_TYPE);
        this.entityTypes.put("mangrove", BuganairMod.BUGANAIR_MANGROVE_BOAT_ENTITY_TYPE);
        this.entityTypes.put("bamboo", BuganairMod.BUGANAIR_BAMBOO_BOAT_ENTITY_TYPE);
        this.entityTypes.put("cherry", BuganairMod.BUGANAIR_CHERRY_BOAT_ENTITY_TYPE);
        this.entityTypes.put("pale_oak", BuganairMod.BUGANAIR_PALE_OAK_BOAT_ENTITY_TYPE);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        HitResult hitResult = user.raycast(8.0D, 1.0F, false);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return ActionResult.PASS;
        }

        if (!world.isClient()) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;

            BuganairBoatEntity boat = new BuganairBoatEntity(this.entityTypes.get(this.variant), world);

            // Set your custom string variant
            boat.setVariant(this.variant);

            boat.refreshPositionAndAngles(
                    blockHitResult.getPos().x,
                    blockHitResult.getPos().y + 0.5D,
                    blockHitResult.getPos().z,
                    user.getYaw(),
                    0.0F
            );

            world.spawnEntity(boat);

            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }
        return ActionResult.SUCCESS;
    }
}