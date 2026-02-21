package pink.iika.extrapotions.mixin

import net.minecraft.entity.player.HungerManager
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(HungerManager::class)
interface ExhaustionAccessor {

    @get:Accessor
    @set:Accessor
    var exhaustion: Float
}