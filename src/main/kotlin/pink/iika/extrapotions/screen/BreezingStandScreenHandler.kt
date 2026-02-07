package pink.iika.extrapotions.screen

import net.minecraft.advancement.criterion.Criteria
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import pink.iika.extrapotions.item.ModItems
import pink.iika.extrapotions.recipe.BreezingRecipeRegistry

class BreezingStandScreenHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: PlayerInventory,
    inventory: Inventory = SimpleInventory(6),
    propertyDelegate: PropertyDelegate = ArrayPropertyDelegate(2),
    val brewingRecipeRegistry: BreezingRecipeRegistry
) : ScreenHandler(ModScreenHandlers.BREEZING_STAND_BLOCK_SCREEN_HANDLER_TYPE, syncId) {
    private val inventory: Inventory
    private val propertyDelegate: PropertyDelegate
    private val ingredientSlot1: Slot
    private val ingredientSlot2: Slot

    constructor(
        syncId: Int,
        playerInventory: PlayerInventory
    ) : this(
        syncId,
        playerInventory,
        SimpleInventory(6),
        ArrayPropertyDelegate(2),
        BreezingRecipeRegistry.EMPTY
    )

    init {
        checkSize(inventory, 6)
        checkDataCount(propertyDelegate, 2)
        this.inventory = inventory
        this.propertyDelegate = propertyDelegate
        this.addSlot(PotionSlot(inventory, 0, 56, 51))
        this.addSlot(PotionSlot(inventory, 1, 79, 58))
        this.addSlot(PotionSlot(inventory, 2, 102, 51))
        this.ingredientSlot1 = this.addSlot(IngredientSlot(brewingRecipeRegistry, inventory, 3, 71, 17))
        this.ingredientSlot2 = this.addSlot(IngredientSlot(brewingRecipeRegistry, inventory, 4, 87, 17))
        this.addSlot(FuelSlot(inventory, 5, 17, 17))
        this.addProperties(propertyDelegate)
        this.addPlayerSlots(playerInventory, 8, 84)
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return this.inventory.canPlayerUse(player)
    }

    override fun quickMove(player: PlayerEntity, slotIndex: Int): ItemStack {
        val slot = this.slots.getOrNull(slotIndex) ?: return ItemStack.EMPTY
        if (!slot.hasStack()) return ItemStack.EMPTY

        val stack = slot.stack
        val original = stack.copy()

        when {
            slotIndex >= 6 -> {
                when {
                    brewingRecipeRegistry.isValidIngredient(stack) -> {
                        if (!this.insertItem(stack, 3, 5, false)) return ItemStack.EMPTY
                    }

                    FuelSlot.matches(stack) -> {
                        if (!this.insertItem(stack, 5, 6, false)) return ItemStack.EMPTY
                    }

                    PotionSlot.matches(stack) -> {
                        if (!this.insertItem(stack, 0, 3, false)) return ItemStack.EMPTY
                    }

                    slotIndex in 6 until 32 -> {
                        if (!this.insertItem(stack, 33, 42, false)) return ItemStack.EMPTY
                    }

                    slotIndex in 32 until 42 -> {
                        if (!this.insertItem(stack, 6, 33, false)) return ItemStack.EMPTY
                    }
                }
            }

            else -> {
                if (!this.insertItem(stack, 6, 42, true)) return ItemStack.EMPTY
                slot.onQuickTransfer(stack, original)
            }
        }

        if (stack.isEmpty) {
            slot.stack = ItemStack.EMPTY
        } else {
            slot.markDirty()
        }

        if (stack.count == original.count) return ItemStack.EMPTY

        slot.onTakeItem(player, stack)
        return original
    }


    val fuel: Int
        get() = this.propertyDelegate.get(1)

    val brewTime: Int
        get() = this.propertyDelegate.get(0)

    internal class PotionSlot(inventory: Inventory?, i: Int, j: Int, k: Int) : Slot(inventory, i, j, k) {
        override fun canInsert(stack: ItemStack): Boolean {
            return matches(stack)
        }

        override fun getMaxItemCount(): Int {
            return 1
        }

        override fun onTakeItem(player: PlayerEntity?, stack: ItemStack) {
            val potion = stack
                .getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT)
                .potion()

            if (player is ServerPlayerEntity && potion.isPresent) {
                Criteria.BREWED_POTION.trigger(player, potion.get())
            }

            super.onTakeItem(player, stack)
        }

        override fun getBackgroundSprite(): Identifier {
            return EMPTY_POTION_SLOT_TEXTURE
        }

        companion object {
            fun matches(stack: ItemStack): Boolean {
                return stack.isOf(ModItems.AMETHYST_POTION) || stack.isOf(ModItems.AMETHYST_SPLASH_POTION) || stack.isOf(ModItems.AMETHYST_LINGERING_POTION) || stack.isOf(
                    ModItems.AMETHYST_BOTTLE
                )
            }
        }
    }

    internal class IngredientSlot(
        private val brewingRecipeRegistry: BreezingRecipeRegistry,
        inventory: Inventory?,
        index: Int,
        x: Int,
        y: Int
    ) : Slot(inventory, index, x, y) {
        override fun canInsert(stack: ItemStack): Boolean {
            return this.brewingRecipeRegistry.isValidIngredient(stack)
        }
    }

    internal class FuelSlot(inventory: Inventory?, i: Int, j: Int, k: Int) : Slot(inventory, i, j, k) {
        override fun canInsert(stack: ItemStack): Boolean {
            return matches(stack)
        }

        override fun getBackgroundSprite(): Identifier {
            return EMPTY_BREWING_FUEL_SLOT_TEXTURE
        }

        companion object {
            fun matches(stack: ItemStack): Boolean {
                return stack.isOf(ModItems.BREEZE_POWDER)
            }
        }
    }

    companion object {
        val EMPTY_BREWING_FUEL_SLOT_TEXTURE: Identifier = Identifier.ofVanilla("container/slot/brewing_fuel")
        val EMPTY_POTION_SLOT_TEXTURE: Identifier = Identifier.ofVanilla("container/slot/potion")
        private const val field_30763 = 0
        private const val field_30764 = 2
        private const val INGREDIENT_SLOT_ID = 3
        private const val FUEL_SLOT_ID = 4
        private const val BREWING_STAND_INVENTORY_SIZE = 5
        private const val PROPERTY_COUNT = 2
        private const val INVENTORY_START = 5
        private const val INVENTORY_END = 32
        private const val HOTBAR_START = 32
        private const val HOTBAR_END = 41
    }
}