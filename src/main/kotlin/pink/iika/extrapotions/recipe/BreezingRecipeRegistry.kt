package pink.iika.extrapotions.recipe

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder
import net.minecraft.block.Blocks
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.PotionItem
import net.minecraft.potion.Potion
import net.minecraft.potion.Potions
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.resource.featuretoggle.FeatureSet
import pink.iika.extrapotions.item.ModItems
import pink.iika.extrapotions.potion.ModPotions

class BreezingRecipeRegistry internal constructor(
    private val potionTypes: List<Ingredient>,
    private val potionRecipes: List<Recipe<Potion>>,
    private val itemRecipes: List<Recipe<Item>>
) {
    fun isValidIngredient(stack: ItemStack): Boolean {
        return this.isItemRecipeIngredient(stack) || this.isPotionRecipeIngredient(stack)
    }

    private fun isPotionType(stack: ItemStack): Boolean {
        for (ingredient in this.potionTypes) {
            if (ingredient.test(stack)) {
                return true
            }
        }

        return false
    }

    fun isItemRecipeIngredient(stack: ItemStack): Boolean {
        for (recipe in this.itemRecipes) {
            if (recipe.ingredient1.test(stack) || recipe.ingredient2.test(stack)) {
                return true
            }
        }

        return false
    }

    fun isPotionRecipeIngredient(stack: ItemStack): Boolean {
        for (recipe in this.potionRecipes) {
            if (recipe.ingredient1.test(stack) || recipe.ingredient2.test(stack)) {
                return true
            }
        }

        return false
    }

    fun isBrewable(potion: RegistryKey<Potion>): Boolean {
        for (recipe in potionRecipes) {
            if (recipe.to == potion) {
                return true
            }
        }
        return false
    }

    fun hasRecipe(input: ItemStack, ingredient1: ItemStack, ingredient2: ItemStack): Boolean {
        return if (!this.isPotionType(input)) {
            false
        } else {
            this.hasItemRecipe(input, ingredient1, ingredient2) || this.hasPotionRecipe(input, ingredient1, ingredient2)
        }
    }

    fun hasItemRecipe(input: ItemStack, ingredient1: ItemStack, ingredient2: ItemStack): Boolean {
        val inputKey = Registries.ITEM.getKey(input.item).orElse(null) ?: return false

        for (recipe in itemRecipes) {
            if (recipe.from == inputKey && twoIngredientTest(recipe, ingredient1, ingredient2)) {
                return true
            }
        }

        return false
    }

    fun hasPotionRecipe(input: ItemStack, ingredient1: ItemStack, ingredient2: ItemStack): Boolean {
        val potionEntry = input
            .getOrDefault(
                DataComponentTypes.POTION_CONTENTS,
                PotionContentsComponent.DEFAULT
            )
            .potion()
            .orElse(null)
            ?: return false

        val potionKey = potionEntry.key.orElse(null) ?: return false

        for (recipe in potionRecipes) {
            if (recipe.from == potionKey && twoIngredientTest(recipe, ingredient1, ingredient2)) {
                return true
            }
        }

        return false
    }

    fun craft(ingredient1: ItemStack, ingredient2: ItemStack, input: ItemStack): ItemStack {
        if (input.isEmpty) return input

        val potionEntry = input
            .getOrDefault(
                DataComponentTypes.POTION_CONTENTS,
                PotionContentsComponent.DEFAULT
            )
            .potion()
            .orElse(null)
            ?: return input

        // Item recipes: bottle changes, potion stays
        val inputKey = Registries.ITEM.getKey(input.item).orElseThrow()

        for (recipe in itemRecipes) {
            if (recipe.from == inputKey && twoIngredientTest(recipe, ingredient1, ingredient2)) {
                return PotionContentsComponent.createStack(
                    Registries.ITEM.get(recipe.to),
                    potionEntry
                )
            }
        }

        // Potion recipes: potion changes, bottle stays
        for (recipe in potionRecipes) {
            if (recipe.from == potionEntry.key.orElseThrow() &&
                twoIngredientTest(recipe, ingredient1, ingredient2)
            ) {
                val outputEntry = Registries.POTION.getOrThrow(recipe.to)

                return PotionContentsComponent.createStack(
                    input.item,
                    outputEntry
                )
            }
        }

        return input
    }

    private fun <T : Any> twoIngredientTest(recipe: Recipe<T>, ingredient1: ItemStack, ingredient2: ItemStack): Boolean {
        val scenario1 = recipe.ingredient1.test(ingredient1) && recipe.ingredient2.test(ingredient2)
        val scenario2 = recipe.ingredient1.test(ingredient2) && recipe.ingredient2.test(ingredient1)
        return scenario1 || scenario2
    }

    class Builder(private val enabledFeatures: FeatureSet?) : FabricBrewingRecipeRegistryBuilder {
        private val potionTypes: MutableList<Ingredient> = mutableListOf()
        private val potionRecipes: MutableList<Recipe<Potion>> = mutableListOf()
        private val itemRecipes: MutableList<Recipe<Item>> = mutableListOf()

        fun registerItemRecipe(input: Item, ingredient1: Item, ingredient2: Item, output: Item) {
            if (input.isEnabled(this.enabledFeatures) && ingredient1.isEnabled(this.enabledFeatures) && ingredient2.isEnabled(this.enabledFeatures) && output.isEnabled(
                    this.enabledFeatures
                )
            ) {
                assertPotion(input)
                assertPotion(output)
                this.itemRecipes.add(
                    Recipe(
                        Registries.ITEM.getKey(input).orElseThrow(),
                        Ingredient.ofItem(ingredient1),
                        Ingredient.ofItem(ingredient2),
                        Registries.ITEM.getKey(output).orElseThrow()
                    )
                )
            }
        }

        fun registerPotionType(item: Item) {
            if (item.isEnabled(this.enabledFeatures)) {
                assertPotion(item)
                this.potionTypes.add(Ingredient.ofItem(item))
            }
        }

        fun registerPotionRecipe(
            input: RegistryKey<Potion>,
            ingredient1: Item,
            ingredient2: Item,
            output: RegistryKey<Potion>
        ) {
            if (ingredient1.isEnabled(enabledFeatures) && ingredient2.isEnabled(enabledFeatures)) {
                potionRecipes.add(
                    Recipe(
                        input,
                        Ingredient.ofItem(ingredient1),
                        Ingredient.ofItem(ingredient2),
                        output
                    )
                )
            }
        }

        fun registerRecipes(ingredient1: Item, ingredient2: Item, potion: RegistryKey<Potion>) {
            val potionValue = Registries.POTION.get(potion)
            if (potionValue!!.isEnabled(enabledFeatures)) {
                registerPotionRecipe(Potions.WATER.key.get(), ingredient1, ingredient2, Potions.MUNDANE.key.get())
                registerPotionRecipe(Potions.AWKWARD.key.get(), ingredient1, ingredient2, potion)
            }
        }

        fun build(): BreezingRecipeRegistry {
            return BreezingRecipeRegistry(
                potionTypes.toList(),
                potionRecipes.toList(),
                itemRecipes.toList()
            )
        }

        companion object {
            private fun assertPotion(potionType: Item?) {
                require(potionType is PotionItem) {
                    "Expected a potion, got: " + Registries.ITEM.getId(potionType).toString()
                }
            }
        }
    }

    internal data class Recipe<T>(
        val from: RegistryKey<T>,
        val ingredient1: Ingredient,
        val ingredient2: Ingredient,
        val to: RegistryKey<T>
    )

    companion object {
        const val FUEL: Int = 20
        val EMPTY: BreezingRecipeRegistry = BreezingRecipeRegistry(
            mutableListOf(),
            mutableListOf(),
            mutableListOf()
        )

        fun create(enabledFeatures: FeatureSet?): BreezingRecipeRegistry {
            val builder = Builder(enabledFeatures)
            registerDefaults(builder)
            return builder.build()
        }

        fun registerDefaults(builder: Builder) {
            fun key(potion: RegistryEntry<Potion>): RegistryKey<Potion> =
                potion.key.orElseThrow()

            // Potion containers
            builder.registerPotionType(ModItems.AMETHYST_POTION)
            builder.registerPotionType(ModItems.AMETHYST_SPLASH_POTION)
            builder.registerPotionType(ModItems.AMETHYST_LINGERING_POTION)

            // Bottle upgrades
            builder.registerItemRecipe(ModItems.AMETHYST_POTION, Items.GUNPOWDER, Items.WIND_CHARGE, ModItems.AMETHYST_SPLASH_POTION)
            builder.registerItemRecipe(ModItems.AMETHYST_SPLASH_POTION, Items.DRAGON_BREATH, Items.WIND_CHARGE, ModItems.AMETHYST_LINGERING_POTION)

            // Base conversions
            builder.registerPotionRecipe(key(Potions.WATER), Items.GLOWSTONE_DUST, Items.WIND_CHARGE, key(Potions.THICK))
            builder.registerPotionRecipe(key(Potions.WATER), Items.REDSTONE, Items.WIND_CHARGE, key(Potions.MUNDANE))
            builder.registerPotionRecipe(key(Potions.WATER), Items.NETHER_WART, Items.WIND_CHARGE, key(Potions.AWKWARD))
            builder.registerPotionRecipe(key(Potions.WATER), ModItems.WARPED_WART, Items.WIND_CHARGE, key(ModPotions.GALE))

            builder.registerPotionRecipe(key(ModPotions.GALE), Items.GLOWSTONE, Items.GLOW_BERRIES, key(ModPotions.GLOWING))
            builder.registerPotionRecipe(key(ModPotions.GLOWING), Items.REDSTONE, Items.WIND_CHARGE, key(ModPotions.LONG_GLOWING))

            builder.registerPotionRecipe(key(ModPotions.GALE), Items.GOLD_INGOT, Items.APPLE, key(ModPotions.ABSORPTION))
            builder.registerPotionRecipe(key(ModPotions.ABSORPTION), Items.REDSTONE, Items.WIND_CHARGE, key(ModPotions.LONG_ABSORPTION))
            builder.registerPotionRecipe(key(ModPotions.ABSORPTION), Items.GLOWSTONE_DUST, Items.WIND_CHARGE, key(ModPotions.STRONG_ABSORPTION))

            builder.registerPotionRecipe(key(ModPotions.GALE), Items.SHULKER_SHELL, Items.PHANTOM_MEMBRANE, key(ModPotions.LEVITATION))
            builder.registerPotionRecipe(key(ModPotions.LEVITATION), Items.REDSTONE, Items.WIND_CHARGE, key(ModPotions.LONG_LEVITATION))
            builder.registerPotionRecipe(key(ModPotions.LEVITATION), Items.GLOWSTONE_DUST, Items.WIND_CHARGE, key(ModPotions.STRONG_LEVITATION))

            builder.registerPotionRecipe(key(ModPotions.GALE), Items.GOLD_INGOT, Items.IRON_INGOT, key(Potions.LUCK))
            builder.registerPotionRecipe(key(Potions.LUCK), Items.REDSTONE, Items.WIND_CHARGE, key(ModPotions.LONG_LUCK))
            builder.registerPotionRecipe(key(Potions.LUCK), Items.GLOWSTONE_DUST, Items.WIND_CHARGE, key(ModPotions.STRONG_LUCK))
            builder.registerPotionRecipe(key(Potions.LUCK), Items.FERMENTED_SPIDER_EYE, Items.WIND_CHARGE, key(ModPotions.UNLUCK))
            builder.registerPotionRecipe(key(ModPotions.LONG_LUCK), Items.FERMENTED_SPIDER_EYE, Items.WIND_CHARGE, key(ModPotions.LONG_UNLUCK))
            builder.registerPotionRecipe(key(ModPotions.STRONG_LUCK), Items.FERMENTED_SPIDER_EYE, Items.WIND_CHARGE, key(ModPotions.STRONG_UNLUCK))

            builder.registerPotionRecipe(key(ModPotions.GALE), Items.COOKED_BEEF, Items.COOKED_PORKCHOP, key(ModPotions.NOURISHMENT))
            builder.registerPotionRecipe(key(ModPotions.NOURISHMENT), Items.REDSTONE, Items.WIND_CHARGE, key(ModPotions.LONG_NOURISHMENT))
            builder.registerPotionRecipe(key(ModPotions.NOURISHMENT), Items.GLOWSTONE_DUST, Items.WIND_CHARGE, key(ModPotions.STRONG_NOURISHMENT))
            builder.registerPotionRecipe(key(ModPotions.NOURISHMENT), Items.GOLDEN_CARROT, Items.GLISTERING_MELON_SLICE, key(ModPotions.SATURATION))
            builder.registerPotionRecipe(key(ModPotions.LONG_NOURISHMENT), Items.GOLDEN_CARROT, Items.GLISTERING_MELON_SLICE, key(ModPotions.LONG_NOURISHMENT))

            builder.registerPotionRecipe(key(ModPotions.GALE), Blocks.SCULK.asItem(), Items.INK_SAC, key(ModPotions.DARKNESS))
            builder.registerPotionRecipe(key(ModPotions.DARKNESS), Items.REDSTONE, Items.WIND_CHARGE, key(ModPotions.LONG_DARKNESS))

            builder.registerPotionRecipe(key(ModPotions.GALE), Items.INK_SAC, Items.SPIDER_EYE, key(ModPotions.BLINDNESS))
            builder.registerPotionRecipe(key(ModPotions.BLINDNESS), Items.REDSTONE, Items.WIND_CHARGE, key(ModPotions.LONG_BLINDNESS))
        }
    }
}