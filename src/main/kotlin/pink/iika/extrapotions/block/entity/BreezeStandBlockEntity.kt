package pink.iika.extrapotions.block.entity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.LockableContainerBlockEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.BrewingRecipeRegistry
import net.minecraft.registry.tag.ItemTags
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.storage.ReadView
import net.minecraft.storage.WriteView
import net.minecraft.text.Text
import net.minecraft.util.ItemScatterer
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import pink.iika.extrapotions.block.BreezeStandBlock
import pink.iika.extrapotions.item.ModItems
import pink.iika.extrapotions.screen.BreezeStandScreenHandler

open class BreezeStandBlockEntity(pos: BlockPos, state: BlockState?) :
    LockableContainerBlockEntity(ModBlockEntities.BREEZE_STAND_BLOCK_ENTITY_TYPE, pos, state), SidedInventory {
    private var inventory: DefaultedList<ItemStack?>
    var brewTime: Int = 0
    private var slotsEmptyLastTick: BooleanArray? = null
    private var itemBrewing: Item? = null
    var fuel: Int = 0
    protected val propertyDelegate: PropertyDelegate

    init {
        this.inventory = DefaultedList.ofSize<ItemStack?>(5, ItemStack.EMPTY)
        this.propertyDelegate = object : PropertyDelegate {
            override fun get(index: Int): Int {
                val var10000: Int = when (index) {
                    0 -> this@BreezeStandBlockEntity.brewTime
                    1 -> this@BreezeStandBlockEntity.fuel
                    else -> 0
                }

                return var10000
            }

            override fun set(index: Int, value: Int) {
                when (index) {
                    0 -> this@BreezeStandBlockEntity.brewTime = value
                    1 -> this@BreezeStandBlockEntity.fuel = value
                }
            }

            override fun size(): Int {
                return 2
            }
        }
    }

    override fun getContainerName(): Text {
        return CONTAINER_NAME_TEXT
    }

    override fun size(): Int {
        return this.inventory.size
    }

    override fun getHeldStacks(): DefaultedList<ItemStack?> {
        return this.inventory
    }

    override fun setHeldStacks(inventory: DefaultedList<ItemStack?>) {
        this.inventory = inventory
    }

    private val slotsEmpty: BooleanArray
        get() {
            val bls = BooleanArray(3)

            for (i in 0..2) {
                if (!(this.inventory[i] as ItemStack).isEmpty) {
                    bls[i] = true
                }
            }

            return bls
        }

    override fun readData(view: ReadView) {
        super.readData(view)
        this.inventory = DefaultedList.ofSize<ItemStack?>(this.size(), ItemStack.EMPTY)
        Inventories.readData(view, this.inventory)
        this.brewTime = view.getShort("BrewTime", 0.toShort())
        if (this.brewTime > 0) {
            this.itemBrewing = (this.inventory[3] as ItemStack).item
        }

        this.fuel = view.getByte("Fuel", 0.toByte()).toInt()
    }

    override fun writeData(view: WriteView) {
        super.writeData(view)
        view.putShort("BrewTime", this.brewTime.toShort())
        Inventories.writeData(view, this.inventory)
        view.putByte("Fuel", this.fuel.toByte())
    }

    override fun isValid(slot: Int, stack: ItemStack): Boolean {
        when (slot) {
            3 -> {
                val brewingRecipeRegistry =
                    if (this.world != null) this.world!!.brewingRecipeRegistry else BrewingRecipeRegistry.EMPTY
                return brewingRecipeRegistry.isValidIngredient(stack)
            }
            4 -> {
                return stack.isOf(ModItems.BREEZE_POWDER)
            }
            else -> {
                return (stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(
                    ModItems.AMETHYST_BOTTLE
                )) && this.getStack(slot).isEmpty
            }
        }
    }

    override fun getAvailableSlots(side: Direction?): IntArray? {
        return if (side == Direction.UP) {
            TOP_SLOTS
        } else {
            if (side == Direction.DOWN) BOTTOM_SLOTS else SIDE_SLOTS
        }
    }

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        return this.isValid(slot, stack)
    }

    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        return if (slot == 3) stack.isOf(ModItems.AMETHYST_BOTTLE) else true
    }

    override fun createScreenHandler(syncId: Int, playerInventory: PlayerInventory): ScreenHandler {
        return BreezeStandScreenHandler(syncId, playerInventory, this, this.propertyDelegate)
    }

    companion object {
        private const val INPUT_SLOT_INDEX = 3
        private const val FUEL_SLOT_INDEX = 4
        private val TOP_SLOTS = intArrayOf(3)
        private val BOTTOM_SLOTS = intArrayOf(0, 1, 2, 3)
        private val SIDE_SLOTS = intArrayOf(0, 1, 2, 4)
        const val MAX_FUEL_USES: Int = 20
        const val BREW_TIME_PROPERTY_INDEX: Int = 0
        const val FUEL_PROPERTY_INDEX: Int = 1
        const val PROPERTY_COUNT: Int = 2
        private const val DEFAULT_BREW_TIME: Short = 0
        private const val DEFAULT_FUEL: Byte = 0
        private val CONTAINER_NAME_TEXT: Text = Text.translatable("container.breeze_stand")
        fun tick(world: World, pos: BlockPos, state: BlockState, blockEntity: BreezeStandBlockEntity) {
            val itemStack = blockEntity.inventory[4] as ItemStack
            if (blockEntity.fuel <= 0 && itemStack.isOf(ModItems.BREEZE_POWDER)) {
                blockEntity.fuel = 20
                itemStack.decrement(1)
                markDirty(world, pos, state)
            }

            val bl = canCraft(world.brewingRecipeRegistry, blockEntity.inventory)
            val bl2 = blockEntity.brewTime > 0
            val itemStack2 = blockEntity.inventory[3] as ItemStack
            if (bl2) {
                --blockEntity.brewTime
                val bl3 = blockEntity.brewTime == 0
                if (bl3 && bl) {
                    craft(world, pos, blockEntity.inventory)
                } else if (!bl || !itemStack2.isOf(blockEntity.itemBrewing)) {
                    blockEntity.brewTime = 0
                }

                markDirty(world, pos, state)
            } else if (bl && blockEntity.fuel > 0) {
                --blockEntity.fuel
                blockEntity.brewTime = 400
                blockEntity.itemBrewing = itemStack2.item
                markDirty(world, pos, state)
            }

            val bls = blockEntity.slotsEmpty
            if (blockEntity.slotsEmptyLastTick == null || !bls.contentEquals(blockEntity.slotsEmptyLastTick!!)) {
                blockEntity.slotsEmptyLastTick = bls
                var blockState = state
                if (state.block !is BreezeStandBlock) {
                    return
                }

                for (i in BreezeStandBlock.BOTTLE_PROPERTIES.indices) {
                    blockState = blockState.with(
                        BreezeStandBlock.BOTTLE_PROPERTIES[i],
                        bls[i]
                    ) as BlockState
                }

                world.setBlockState(pos, blockState, 2)
            }
        }

        private fun canCraft(brewingRecipeRegistry: BrewingRecipeRegistry, slots: DefaultedList<ItemStack?>): Boolean {
            val itemStack = slots[3] as ItemStack
            if (itemStack.isEmpty) {
                return false
            } else if (!brewingRecipeRegistry.isValidIngredient(itemStack)) {
                return false
            } else {
                for (i in 0..2) {
                    val itemStack2 = slots[i] as ItemStack
                    if (!itemStack2.isEmpty && brewingRecipeRegistry.hasRecipe(itemStack2, itemStack)) {
                        return true
                    }
                }

                return false
            }
        }

        private fun craft(world: World, pos: BlockPos, slots: DefaultedList<ItemStack?>) {
            var itemStack = slots[3] as ItemStack
            val brewingRecipeRegistry = world.brewingRecipeRegistry

            for (i in 0..2) {
                slots[i] = brewingRecipeRegistry.craft(itemStack, slots[i])
            }

            itemStack.decrement(1)
            val itemStack2 = itemStack.item.recipeRemainder
            if (!itemStack2.isEmpty) {
                if (itemStack.isEmpty) {
                    itemStack = itemStack2
                } else {
                    ItemScatterer.spawn(
                        world,
                        pos.x.toDouble(),
                        pos.y.toDouble(),
                        pos.z.toDouble(),
                        itemStack2
                    )
                }
            }

            slots[3] = itemStack
            world.syncWorldEvent(1035, pos, 0)
        }
    }
}
