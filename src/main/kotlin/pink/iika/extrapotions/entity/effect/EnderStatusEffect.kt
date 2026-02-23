package pink.iika.extrapotions.entity.effect

import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.BlockPos
import java.lang.Double.min
import kotlin.math.floor
import kotlin.math.max
import kotlin.random.Random

internal class EnderStatusEffect(statusEffectCategory: StatusEffectCategory?, i: Int) :
    StatusEffect(statusEffectCategory, i) {
    override fun applyUpdateEffect(
        world: ServerWorld?,
        entity: LivingEntity?,
        amplifier: Int
    ): Boolean {
        val w = world ?: return true
        if (w.isClient) return true
        val e = entity ?: return true

        val x = e.x
        val y = e.y
        val z = e.z

        val range = min((2 * amplifier + 2).toDouble(), 6.0)
        val deltaX = Random.nextDouble(-range, range)
        val deltaZ = Random.nextDouble(-range, range)

        val targetX = x + deltaX
        val targetZ = z + deltaZ

        val startY = floor(y).toInt()

        val directY = findY(targetX, startY, targetZ, w)

        if (directY != null) {

            val dx = targetX - x
            val dy = directY - y
            val dz = targetZ - z
            val distSq = dx * dx + dy * dy + dz * dz

            if (distSq <= 100.0) {

                e.teleport(targetX, directY + 0.01, targetZ, true)

                w.playSound(
                    null,
                    targetX, directY.toDouble(), targetZ,
                    SoundEvents.ENTITY_PLAYER_TELEPORT,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f
                )

                return true
            }
        }

        val best = findBestPosition(
            x, y, z,
            targetX, targetZ,
            w,
            horizontalRadius = 2
        )

        if (best != null) {
            val (bx, by, bz) = best

            e.teleport(bx, by + 0.01, bz, true)

            w.playSound(
                null,
                bx, by.toDouble(), bz,
                SoundEvents.ENTITY_PLAYER_TELEPORT,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
            )
        }

        return true
    }

    override fun canApplyUpdateEffect(duration: Int, amplifier: Int): Boolean {
        return duration % max(20, 100 shr amplifier) == 0
    }

    fun findY(x: Double, startY: Int, z: Double, world: ServerWorld): Int? {

        val minY = world.bottomY
        val maxY = world.topYInclusive

        if (startY in minY..maxY && isYSafe(x, startY, z, world)) {
            return startY
        }

        var delta = 1

        while (startY - delta >= minY || startY + delta <= maxY) {

            val down = startY - delta
            if (down >= minY && isYSafe(x, down, z, world)) {
                return down
            }

            val up = startY + delta
            if (up <= maxY && isYSafe(x, up, z, world)) {
                return up
            }

            delta++
        }

        return null
    }

    fun isYSafe(x: Double, y: Int, z: Double, world: ServerWorld): Boolean {

        val pos = BlockPos.ofFloored(x, y.toDouble(), z)
        val below = pos.down()
        val above = pos.up()

        val state = world.getBlockState(pos)
        val aboveState = world.getBlockState(above)
        val belowState = world.getBlockState(below)

        val feetFree = !state.isSolidBlock(world, pos)
        val headFree = !aboveState.isSolidBlock(world, above)

        val notLava = state.fluidState.isEmpty ||
                state.fluidState.fluid !== net.minecraft.fluid.Fluids.LAVA

        val belowSupports = belowState.isSolidBlock(world, below) ||
                belowState.fluidState.isEmpty.not()

        return feetFree && headFree && notLava && belowSupports
    }

    fun findBestPosition(
        originX: Double,
        originY: Double,
        originZ: Double,
        targetX: Double,
        targetZ: Double,
        world: ServerWorld,
        horizontalRadius: Int
    ): Triple<Double, Int, Double>? {
        var best: Triple<Double, Int, Double>? = null
        var bestDistSq = Double.MAX_VALUE

        val baseStartY = floor(originY).toInt()

        for (dx in -horizontalRadius..horizontalRadius) {
            for (dz in -horizontalRadius..horizontalRadius) {

                val candidateX = targetX + dx
                val candidateZ = targetZ + dz

                val safeY = findY(candidateX, baseStartY, candidateZ, world)
                if (safeY != null) {

                    val dX = candidateX - originX
                    val dY = safeY - originY
                    val dZ = candidateZ - originZ

                    val distSq = dX * dX + dY * dY + dZ * dZ

                    if (distSq < bestDistSq) {
                        bestDistSq = distSq
                        best = Triple(candidateX, safeY, candidateZ)
                    }
                }
            }
        }

        return best
    }
}