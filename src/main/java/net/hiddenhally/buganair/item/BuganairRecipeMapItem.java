package net.hiddenhally.buganair.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
// Added for 1.21.2+ refactor
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

// Added for the new textConsumer


public class BuganairRecipeMapItem extends Item {

    public BuganairRecipeMapItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient()) {
            openScreen(); // mai chiamato lato server grazie a isClient()
        }
        return ActionResult.SUCCESS;
    }

    // Il metodo viene strip-pato lato server da Fabric grazie all'annotazione
    @Environment(EnvType.CLIENT)
    private static void openScreen() {
        net.minecraft.client.MinecraftClient.getInstance()
                .setScreen(new net.hiddenhally.buganair.client.BuganairRecipeScreen());
    }
}