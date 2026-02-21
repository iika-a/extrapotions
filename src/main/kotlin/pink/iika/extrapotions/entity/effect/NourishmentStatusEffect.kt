package pink.iika.extrapotions.entity.effect

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import pink.iika.extrapotions.mixin.ExhaustionAccessor

internal class NourishmentStatusEffect(statusEffectCategory: StatusEffectCategory?, i: Int) :
    StatusEffect(statusEffectCategory, i) {
    override fun applyUpdateEffect(world: ServerWorld?, entity: LivingEntity?, amplifier: Int): Boolean {
        if (entity is PlayerEntity) {
            val hunger = entity.hungerManager
            val accessor = hunger as ExhaustionAccessor

            val newExhaustion = accessor.exhaustion - 0.005f * (amplifier + 1)
            accessor.exhaustion = maxOf(0f, newExhaustion)
        }

        return true
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        return true
    }
}