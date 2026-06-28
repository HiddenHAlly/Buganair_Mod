package net.hiddenhally.buganair.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class BuganairSniperItem extends Item {

    public BuganairSniperItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        // Tutta la logica di mira/zoom/sparo è client-side (vedi BuganairModClient
        // + BuganairSniperClientState): qui evitiamo solo qualsiasi azione "use"
        // di default di vanilla. In pratica questo metodo non verrà nemmeno
        // chiamato per il tasto destro, perché lo intercettiamo prima noi.
        return ActionResult.CONSUME;
    }
}