package net.hiddenhally.buganair.item;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

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

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context,
                              TooltipDisplayComponent displayComponent,
                              Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.buganair.buganair_sniper.tooltip_1")
                .formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable("item.buganair.buganair_sniper.tooltip_2")
                .formatted(Formatting.YELLOW));
    }
}