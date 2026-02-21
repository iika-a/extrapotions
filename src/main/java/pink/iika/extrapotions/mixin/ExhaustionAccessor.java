package pink.iika.extrapotions.mixin;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HungerManager.class)
public interface ExhaustionAccessor {

    @Accessor("exhaustion")
    float getExhaustion();

    @Accessor("exhaustion")
    void setExhaustion(float exhaustion);
}
