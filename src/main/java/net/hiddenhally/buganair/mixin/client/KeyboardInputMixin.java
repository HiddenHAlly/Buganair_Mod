package net.hiddenhally.buganair.mixin.client;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Precedentemente usato per cancellare lo strafe (A/D) durante il lean
 * con Shift, evitando che il giocatore si muovesse lateralmente mentre
 * inclinava la camera.
 *
 * Con il nuovo sistema il lean è sulle frecce ← → e non interferisce
 * con WASD, quindi questo mixin non deve più sovrascrivere il movimento.
 *
 * Il file è mantenuto come placeholder: puoi aggiungere qui eventuali
 * limitazioni di movimento future (es. niente sprint mentre sei in prone).
 */
@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {
    // Intenzionalmente vuoto.
}