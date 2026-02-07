package pink.iika.extrapotions.block.entity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.LockableContainerBlockEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import pink.iika.extrapotions.block.BreezingStandBlock
import pink.iika.extrapotions.item.ModItems
import pink.iika.extrapotions.recipe.BreezingRecipeRegistry
import pink.iika.extrapotions.screen.BreezingStandScreenHandler

open class BreezingStandBlockEntity(pos: BlockPos, state: BlockState?) :
    LockableContainerBlockEntity(
        ModBlockEntities.BREEZING_STAND_BLOCK_ENTITY_TYPE,
        pos,
        state
    ),
    SidedInventory {

    private var inventory: DefaultedList<ItemStack?> =
        DefaultedList.ofSize(6, ItemStack.EMPTY)

    var brewTime = 0
    var fuel = 0

    private var slotsEmptyLastTick: BooleanArray? = null
    private var itemBrewingA: Item? = null
    private var itemBrewingB: Item? = null

    protected val propertyDelegate: PropertyDelegate = object : PropertyDelegate {
        override fun get(index: Int): Int =
            when (index) {
                0 -> brewTime
                1 -> fuel
                else -> 0
            }

        override fun set(index: Int, value: Int) {
            when (index) {
                0 -> brewTime = value
                1 -> fuel = value
            }
        }

        override fun size() = 2
    }

    override fun getContainerName(): Text = CONTAINER_NAME_TEXT
    override fun size(): Int = inventory.size
    override fun getHeldStacks(): DefaultedList<ItemStack?> = inventory
    override fun setHeldStacks(inventory: DefaultedList<ItemStack?>) {
        this.inventory = inventory
    }

    private val slotsEmpty: BooleanArray
        get() {
            val present = BooleanArray(3)
            for (i in 0..2) {
                present[i] = !(inventory[i] as ItemStack).isEmpty
            }
            return present
        }

    override fun readData(view: ReadView) {
        super.readData(view)
        inventory = DefaultedList.ofSize(size(), ItemStack.EMPTY)
        Inventories.readData(view, inventory)

        brewTime = view.getShort("BrewTime", 0)
        fuel = view.getByte("Fuel", 0).toInt()

        if (brewTime > 0) {
            itemBrewingA = inventory[3]?.item
            itemBrewingB = inventory[4]?.item
        }
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        view.putShort("BrewTime", brewTime.toShort())
        view.putByte("Fuel", fuel.toByte())
        Inventories.writeData(view, inventory)
    }

    override fun isValid(slot: Int, stack: ItemStack): Boolean {
        val registry =
            if (world != null)
                BreezingRecipeRegistry.create(world!!.enabledFeatures)
            else
                BreezingRecipeRegistry.EMPTY

        return when (slot) {
            3, 4 -> registry.isValidIngredient(stack)
            5 -> stack.isOf(ModItems.BREEZE_POWDER)
            else -> (
                    stack.isOf(ModItems.AMETHYST_POTION) ||
                            stack.isOf(ModItems.AMETHYST_SPLASH_POTION) ||
                            stack.isOf(ModItems.AMETHYST_LINGERING_POTION) ||
                            stack.isOf(ModItems.AMETHYST_BOTTLE)
                    ) && getStack(slot).isEmpty
        }
    }

    override fun getAvailableSlots(side: Direction?): IntArray =
        when (side) {
            Direction.UP -> TOP_SLOTS
            Direction.DOWN -> BOTTOM_SLOTS
            else -> SIDE_SLOTS
        }

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?) =
        isValid(slot, stack)

    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction?) =
        !(slot == 3 || slot == 4) || stack.isOf(ModItems.AMETHYST_BOTTLE)

    override fun createScreenHandler(
        syncId: Int,
        playerInventory: PlayerInventory
    ): ScreenHandler =
        BreezingStandScreenHandler(syncId, playerInventory, this, propertyDelegate, BreezingRecipeRegistry.create(world!!.enabledFeatures))

    companion object {
        private val TOP_SLOTS = intArrayOf(3, 4)
        private val BOTTOM_SLOTS = intArrayOf(0, 1, 2)
        private val SIDE_SLOTS = intArrayOf(5)

        const val MAX_FUEL_USES = 20
        private val CONTAINER_NAME_TEXT =
            Text.translatable("container.breezing_stand")

        fun tick(
            world: World,
            pos: BlockPos,
            state: BlockState,
            be: BreezingStandBlockEntity
        ) {
            val fuelStack = be.inventory[5] as ItemStack
            if (be.fuel <= 0 && fuelStack.isOf(ModItems.BREEZE_POWDER)) {
                be.fuel = MAX_FUEL_USES
                fuelStack.decrement(1)
                markDirty(world, pos, state)
            }

            val registry = BreezingRecipeRegistry.create(world.enabledFeatures)
            val canCraft = canCraft(registry, be.inventory)
            val brewing = be.brewTime > 0

            val ingA = be.inventory[3] as ItemStack
            val ingB = be.inventory[4] as ItemStack

            if (brewing) {
                be.brewTime--

                if (be.brewTime == 0 && canCraft) {
                    craft(world, pos, registry, be.inventory)
                } else if (
                    !canCraft ||
                    ingA.item != be.itemBrewingA ||
                    ingB.item != be.itemBrewingB
                ) {
                    be.brewTime = 0
                }

                markDirty(world, pos, state)
            } else if (canCraft && be.fuel > 0) {
                be.fuel--
                be.brewTime = 400
                be.itemBrewingA = ingA.item
                be.itemBrewingB = ingB.item
                markDirty(world, pos, state)
            }

            val present = be.slotsEmpty
            if (be.slotsEmptyLastTick == null ||
                !present.contentEquals(be.slotsEmptyLastTick!!)
            ) {
                be.slotsEmptyLastTick = present
                var newState = state
                if (state.block is BreezingStandBlock) {
                    for (i in BreezingStandBlock.BOTTLE_PROPERTIES.indices) {
                        newState = newState.with(
                            BreezingStandBlock.BOTTLE_PROPERTIES[i],
                            present[i]
                        ) as BlockState
                    }
                    world.setBlockState(pos, newState, 2)
                }
            }
        }

        private fun canCraft(
            registry: BreezingRecipeRegistry,
            slots: DefaultedList<ItemStack?>
        ): Boolean {
            val a = slots[3] as ItemStack
            val b = slots[4] as ItemStack
            if (a.isEmpty || b.isEmpty) return false

            for (i in 0..2) {
                val bottle = slots[i] as ItemStack
                if (!bottle.isEmpty &&
                    registry.hasRecipe(bottle, a, b)
                ) return true
            }
            return false
        }

        private fun craft(
            world: World,
            pos: BlockPos,
            registry: BreezingRecipeRegistry,
            slots: DefaultedList<ItemStack?>
        ) {
            val a = slots[3] ?: return
            val b = slots[4] ?: return

            for (i in 0..2) {
                val bottle = slots[i]
                if (bottle != null && !bottle.isEmpty) {
                    slots[i] = registry.craft(a, b, bottle)
                }
            }

            a.decrement(1)
            b.decrement(1)

            slots[3] = a
            slots[4] = b

            world.syncWorldEvent(1035, pos, 0)
        }
    }
}
