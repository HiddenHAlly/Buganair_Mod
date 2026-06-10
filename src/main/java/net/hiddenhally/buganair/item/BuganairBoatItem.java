package net.hiddenhally.buganair.item;

import net.hiddenhally.buganair.BuganairMod;
import net.hiddenhally.buganair.entity.BuganairBoatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class BuganairBoatItem extends Item {
    public BuganairBoatItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        HitResult hitResult = user.raycast(8.0D, 1.0F, false);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(stack);
        }

        if (!world.isClient) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BuganairBoatEntity boat = new BuganairBoatEntity(BuganairMod.BUGANAIR_BOAT_ENTITY_TYPE, world);
            boat.refreshPositionAndAngles(
                blockHitResult.getPos().x,
                blockHitResult.getPos().y + 0.5D,
                blockHitResult.getPos().z,
                user.getYaw(),
                0.0F
            );

            world.spawnEntity(boat);
            user.startRiding(boat, true);

            if (!user.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}
