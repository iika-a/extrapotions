package pink.iika.extrapotions.block

import com.mojang.serialization.MapCodec
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.Properties
import net.minecraft.state.property.Property
import net.minecraft.util.ActionResult
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import pink.iika.extrapotions.block.entity.BreezeStandBlockEntity
import pink.iika.extrapotions.block.entity.ModBlockEntities

class BreezeStandBlock(settings: Settings) : BlockWithEntity(settings) {
    public override fun getCodec(): MapCodec<BreezeStandBlock?> {
        return createCodec { BreezeStandBlock(it) }
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BreezeStandBlockEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        world: World,
        state: BlockState?,
        type: BlockEntityType<T?>?
    ): BlockEntityTicker<T?>? {
        return if (world.isClient) null else validateTicker<BreezeStandBlockEntity?, T?>(
            type,
            ModBlockEntities.BREEZE_STAND_BLOCK_ENTITY_TYPE,
            BlockEntityTicker { world: World, pos: BlockPos, state: BlockState, blockEntity: BreezeStandBlockEntity ->
                BreezeStandBlockEntity.tick(
                    world,
                    pos,
                    state,
                    blockEntity
                )
            })
    }

    override fun getOutlineShape(
        state: BlockState?,
        world: BlockView?,
        pos: BlockPos?,
        context: ShapeContext?
    ): VoxelShape? {
        return SHAPE
    }

    override fun onUse(
        state: BlockState?,
        world: World,
        pos: BlockPos?,
        player: PlayerEntity,
        hit: BlockHitResult?
    ): ActionResult {
        if (!world.isClient) {
            val var7 = world.getBlockEntity(pos)
            if (var7 is BreezeStandBlockEntity) {
                player.openHandledScreen(var7)
            }
        }

        return ActionResult.SUCCESS
    }

    override fun randomDisplayTick(state: BlockState?, world: World, pos: BlockPos, random: Random) {
        val d = pos.x.toDouble() + 0.4 + random.nextFloat().toDouble() * 0.2
        val e = pos.y.toDouble() + 0.7 + random.nextFloat().toDouble() * 0.3
        val f = pos.z.toDouble() + 0.4 + random.nextFloat().toDouble() * 0.2
        world.addParticleClient(ParticleTypes.SMALL_GUST, d, e, f, 0.0, 10.0, 0.0)
    }

    override fun onStateReplaced(state: BlockState, world: ServerWorld, pos: BlockPos?, moved: Boolean) {
        ItemScatterer.onStateReplaced(state, world, pos)
    }

    override fun hasComparatorOutput(state: BlockState?): Boolean {
        return true
    }

    override fun getComparatorOutput(state: BlockState?, world: World, pos: BlockPos?, direction: Direction?): Int {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos))
    }

    override fun appendProperties(builder: StateManager.Builder<Block?, BlockState?>) {
        builder.add(*arrayOf<Property<*>?>(BOTTLE_PROPERTIES[0], BOTTLE_PROPERTIES[1], BOTTLE_PROPERTIES[2]))
    }

    override fun canPathfindThrough(state: BlockState?, type: NavigationType?): Boolean {
        return false
    }

    init {
        this.defaultState = (((this.stateManager.getDefaultState() as BlockState).with(
            BOTTLE_PROPERTIES[0],
            false
        ) as BlockState).with(
            BOTTLE_PROPERTIES[1],
            false
        ) as BlockState).with(
            BOTTLE_PROPERTIES[2],
            false
        )
    }

    companion object {
        val BOTTLE_PROPERTIES: Array<BooleanProperty?> =
            arrayOf(Properties.HAS_BOTTLE_0, Properties.HAS_BOTTLE_1, Properties.HAS_BOTTLE_2)
        private val SHAPE: VoxelShape? =
            VoxelShapes.union(createColumnShape(2.0, 2.0, 14.0), createColumnShape(14.0, 0.0, 2.0))
    }
}