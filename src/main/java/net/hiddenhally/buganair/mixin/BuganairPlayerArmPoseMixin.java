package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.client.BuganairSniperClientState;
import net.hiddenhally.buganair.item.BuganairSniperItem;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class BuganairPlayerArmPoseMixin {

    // Update the descriptor to match the game's expectations (PlayerLikeEntity, Arm)
    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void buganair$setSniperPose(PlayerLikeEntity player, Arm arm, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        // We only want to apply this if it's actually a player
        if (player instanceof PlayerEntity playerEntity) {
            // Check the item in the hand corresponding to the Arm (Main hand vs Offhand)
            // Note: If you want to be precise, check if the arm matches the main hand
            if (playerEntity.getMainHandStack().getItem() instanceof BuganairSniperItem) {
                if (playerEntity == net.minecraft.client.MinecraftClient.getInstance().player && BuganairSniperClientState.isAiming()) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.SPYGLASS);
                }
            }
        }
    }
}