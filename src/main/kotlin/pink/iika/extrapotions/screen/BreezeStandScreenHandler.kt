package pink.iika.extrapotions.screen

import net.minecraft.advancement.criterion.Criteria
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.BrewingRecipeRegistry
import net.minecraft.registry.tag.ItemTags
import net.minecraft.screen.ArrayPropertyDelegate
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class BreezeStandScreenHandler @JvmOverloads constructor(
    syncId: Int,
    playerInventory: PlayerInventory,
    inventory: Inventory = SimpleInventory(5),
    propertyDelegate: PropertyDelegate = ArrayPropertyDelegate(2)
) : ScreenHandler(ModScreenHandlers.BREEZE_STAND_BLOCK_SCREEN_HANDLER_TYPE, syncId) {
    private val inventory: Inventory
    private val propertyDelegate: PropertyDelegate
    private val ingredientSlot: Slot

    init {
        checkSize(inventory, 5)
        checkDataCount(propertyDelegate, 2)
        this.inventory = inventory
        this.propertyDelegate = propertyDelegate
        val brewingRecipeRegistry = playerInventory.player.entityWorld.brewingRecipeRegistry
        this.addSlot(PotionSlot(inventory, 0, 56, 51))
        this.addSlot(PotionSlot(inventory, 1, 79, 58))
        this.addSlot(PotionSlot(inventory, 2, 102, 51))
        this.ingredientSlot = this.addSlot(IngredientSlot(brewingRecipeRegistry, inventory, 3, 79, 17))
        this.addSlot(FuelSlot(inventory, 4, 17, 17))
        this.addProperties(propertyDelegate)
        this.addPlayerSlots(playerInventory, 8, 84)
    }

    override fun canUse(player: PlayerEntity?): Boolean {
        return this.inventory.canPlayerUse(player)
    }

    override fun quickMove(player: PlayerEntity?, slot: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot2 = this.slots[slot]
        if (slot2 != null && slot2.hasStack()) {
            val itemStack2 = slot2.stack
            itemStack = itemStack2.copy()
            if ((slot !in 0..2) && slot != 3 && slot != 4) {
                if (FuelSlot.matches(itemStack)) {
                    if (this.insertItem(
                            itemStack2,
                            4,
                            5,
                            false
                        ) || this.ingredientSlot.canInsert(itemStack2) && !this.insertItem(itemStack2, 3, 4, false)
                    ) {
                        return ItemStack.EMPTY
                    }
                } else if (this.ingredientSlot.canInsert(itemStack2)) {
                    if (!this.insertItem(itemStack2, 3, 4, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (PotionSlot.matches(itemStack)) {
                    if (!this.insertItem(itemStack2, 0, 3, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (slot in 5..<32) {
                    if (!this.insertItem(itemStack2, 32, 41, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (slot in 32..<41) {
                    if (!this.insertItem(itemStack2, 5, 32, false)) {
                        return ItemStack.EMPTY
                    }
                } else if (!this.insertItem(itemStack2, 5, 41, false)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (!this.insertItem(itemStack2, 5, 41, true)) {
                    return ItemStack.EMPTY
                }

                slot2.onQuickTransfer(itemStack2, itemStack)
            }

            if (itemStack2.isEmpty) {
                slot2.stack = ItemStack.EMPTY
            } else {
                slot2.markDirty()
            }

            if (itemStack2.count == itemStack.count) {
                return ItemStack.EMPTY
            }

            slot2.onTakeItem(player, itemStack)
        }

        return itemStack
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
                return stack.isOf(Items.POTION) || stack.isOf(Items.SPLASH_POTION) || stack.isOf(Items.LINGERING_POTION) || stack.isOf(
                    Items.GLASS_BOTTLE
                )
            }
        }
    }

    internal class IngredientSlot(
        private val brewingRecipeRegistry: BrewingRecipeRegistry,
        inventory: Inventory?,
        index: Int,
        x: Int,
        y: Int
    ) : Slot(inventory, index, x, y) {
        override fun canInsert(stack: ItemStack?): Boolean {
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
                return stack.isIn(ItemTags.BREWING_FUEL)
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