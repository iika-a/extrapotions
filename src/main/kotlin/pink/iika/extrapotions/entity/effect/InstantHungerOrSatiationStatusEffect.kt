package pink.iika.extrapotions.entity.effect

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.InstantStatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld

internal class InstantHungerOrSatiationStatusEffect(
    category: StatusEffectCategory?,
    color: Int,
    private val nourishing: Boolean
) : InstantStatusEffect(category, color) {
    override fun applyUpdateEffect(world: ServerWorld?, entity: LivingEntity, amplifier: Int): Boolean {
        if (entity is PlayerEntity) {
            val hunger = entity.hungerManager
            val hungerChange = 4 shl amplifier

            if (this.nourishing) {
                hunger.add(hungerChange, .5f)
            } else {
                hunger.add(-hungerChange, 0f)
            }
        }

        return true
    }

    override fun applyInstantEffect(
        world: ServerWorld?,
        effectEntity: Entity?,
        attacker: Entity?,
        target: LivingEntity,
        amplifier: Int,
        proximity: Double
    ) {
        if (target is PlayerEntity) {
            val hunger = target.hungerManager
            val hungerChange = (proximity * (4 shl amplifier) + 0.5).toInt()

            if (this.nourishing) {
                hunger.add(hungerChange, .5f)
            } else {
                hunger.add(-hungerChange, 0f)
            }
        }
    }
}