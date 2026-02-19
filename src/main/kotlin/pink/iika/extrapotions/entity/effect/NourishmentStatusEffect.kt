package pink.iika.extrapotions.entity.effect

import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld

internal class NourishmentStatusEffect(statusEffectCategory: StatusEffectCategory?, i: Int) :
    StatusEffect(statusEffectCategory, i) {
    override fun applyUpdateEffect(world: ServerWorld?, entity: LivingEntity?, amplifier: Int): Boolean {
        if (entity is PlayerEntity) {
            entity.addExhaustion(-0.005f * (amplifier + 1).toFloat())
        }

        return true
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        return true
    }
}