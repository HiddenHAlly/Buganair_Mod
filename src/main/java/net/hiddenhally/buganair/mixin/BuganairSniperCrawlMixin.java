package net.hiddenhally.buganair.mixin;

import net.hiddenhally.buganair.BuganairMod;
import net.hiddenhally.buganair.client.BuganairSniperClientState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class BuganairSniperCrawlMixin {

    @Inject(method = "updatePose", at = @At("HEAD"), cancellable = true)
    private void buganair$overrideCrawlPose(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        boolean isCrawling;

        // 1. CLIENT-SIDE CHECK: Camera position & local prediction rely on this
        if (player.getEntityWorld().isClient()) {
            isCrawling = BuganairSniperClientState.isCrawling();
        }
        // 2. SERVER-SIDE CHECK: Server physics & hitboxes rely on this
        else {
            // Replace this with however your server tracks the crawl state
            // (e.g., checking a static map where you processed BuganairSniperCrawlPayload)
            isCrawling = BuganairMod.isSniperCrawling(player.getUuid());
        }

        if (isCrawling) {
            player.setPose(EntityPose.SWIMMING); // SWIMMING is vanilla's crawling pose
            ci.cancel(); // Stop vanilla from overwriting it with STANDING/SNEAKING
        }
    }
}