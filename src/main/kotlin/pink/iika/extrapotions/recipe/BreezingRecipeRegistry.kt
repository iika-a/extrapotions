package pink.iika.extrapotions.recipe

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder
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
            if (recipe.ingredient.test(stack)) {
                return true
            }
        }

        return false
    }

    fun isPotionRecipeIngredient(stack: ItemStack): Boolean {
        for (recipe in this.potionRecipes) {
            if (recipe.ingredient.test(stack)) {
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

    fun hasRecipe(input: ItemStack, ingredient: ItemStack): Boolean {
        return if (!this.isPotionType(input)) {
            false
        } else {
            this.hasItemRecipe(input, ingredient) || this.hasPotionRecipe(input, ingredient)
        }
    }

    fun hasItemRecipe(input: ItemStack, ingredient: ItemStack): Boolean {
        val inputKey = Registries.ITEM.getKey(input.item).orElse(null) ?: return false

        for (recipe in itemRecipes) {
            if (recipe.from == inputKey && recipe.ingredient.test(ingredient)) {
                return true
            }
        }

        return false
    }

    fun hasPotionRecipe(input: ItemStack, ingredient: ItemStack): Boolean {
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
            if (recipe.from == potionKey && recipe.ingredient.test(ingredient)) {
                return true
            }
        }

        return false
    }

    fun craft(ingredient: ItemStack, input: ItemStack): ItemStack {
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
            if (recipe.from == inputKey && recipe.ingredient.test(ingredient)) {
                return PotionContentsComponent.createStack(
                    Registries.ITEM.get(recipe.to),
                    potionEntry
                )
            }
        }

        // Potion recipes: potion changes, bottle stays
        for (recipe in potionRecipes) {
            if (recipe.from == potionEntry.key.orElseThrow() &&
                recipe.ingredient.test(ingredient)
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


    class Builder(private val enabledFeatures: FeatureSet?) : FabricBrewingRecipeRegistryBuilder {
        private val potionTypes: MutableList<Ingredient> = mutableListOf()
        private val potionRecipes: MutableList<Recipe<Potion>> = mutableListOf()
        private val itemRecipes: MutableList<Recipe<Item>> = mutableListOf()

        fun registerItemRecipe(input: Item, ingredient: Item, output: Item) {
            if (input.isEnabled(this.enabledFeatures) && ingredient.isEnabled(this.enabledFeatures) && output.isEnabled(
                    this.enabledFeatures
                )
            ) {
                assertPotion(input)
                assertPotion(output)
                this.itemRecipes.add(
                    Recipe(
                        Registries.ITEM.getKey(input).orElseThrow(),
                        Ingredient.ofItem(ingredient),
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
            ingredient: Item,
            output: RegistryKey<Potion>
        ) {
            if (ingredient.isEnabled(enabledFeatures)) {
                potionRecipes.add(
                    Recipe(
                        input,
                        Ingredient.ofItem(ingredient),
                        output
                    )
                )
            }
        }

        fun registerRecipes(ingredient: Item, potion: RegistryKey<Potion>) {
            val potionValue = Registries.POTION.get(potion)
            if (potionValue!!.isEnabled(enabledFeatures)) {
                registerPotionRecipe(Potions.WATER.key.get(), ingredient, Potions.MUNDANE.key.get())
                registerPotionRecipe(Potions.AWKWARD.key.get(), ingredient, potion)
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
        val ingredient: Ingredient,
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
            builder.registerPotionType(Items.POTION)
            builder.registerPotionType(Items.SPLASH_POTION)
            builder.registerPotionType(Items.LINGERING_POTION)

            // Bottle upgrades
            builder.registerItemRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION)
            builder.registerItemRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION)

            // Base conversions
            builder.registerPotionRecipe(key(Potions.WATER), Items.GLOWSTONE_DUST, key(Potions.THICK))
            builder.registerPotionRecipe(key(Potions.WATER), Items.REDSTONE, key(Potions.MUNDANE))
            builder.registerPotionRecipe(key(Potions.WATER), Items.NETHER_WART, key(Potions.AWKWARD))

            builder.registerRecipes(Items.BREEZE_ROD, key(Potions.WIND_CHARGED))
            builder.registerRecipes(Items.SLIME_BLOCK, key(Potions.OOZING))
            builder.registerRecipes(Items.STONE, key(Potions.INFESTED))
            builder.registerRecipes(Items.COBWEB, key(Potions.WEAVING))

            builder.registerPotionRecipe(key(Potions.AWKWARD), Items.GOLDEN_CARROT, key(Potions.NIGHT_VISION))
            builder.registerPotionRecipe(key(Potions.NIGHT_VISION), Items.REDSTONE, key(Potions.LONG_NIGHT_VISION))
            builder.registerPotionRecipe(key(Potions.NIGHT_VISION), Items.FERMENTED_SPIDER_EYE, key(Potions.INVISIBILITY))
            builder.registerPotionRecipe(
                key(Potions.LONG_NIGHT_VISION),
                Items.FERMENTED_SPIDER_EYE,
                key(Potions.LONG_INVISIBILITY)
            )
            builder.registerPotionRecipe(key(Potions.INVISIBILITY), Items.REDSTONE, key(Potions.LONG_INVISIBILITY))

            builder.registerRecipes(Items.MAGMA_CREAM, key(Potions.FIRE_RESISTANCE))
            builder.registerPotionRecipe(key(Potions.FIRE_RESISTANCE), Items.REDSTONE, key(Potions.LONG_FIRE_RESISTANCE))

            builder.registerRecipes(Items.RABBIT_FOOT, key(Potions.LEAPING))
            builder.registerPotionRecipe(key(Potions.LEAPING), Items.REDSTONE, key(Potions.LONG_LEAPING))
            builder.registerPotionRecipe(key(Potions.LEAPING), Items.GLOWSTONE_DUST, key(Potions.STRONG_LEAPING))
            builder.registerPotionRecipe(key(Potions.LEAPING), Items.FERMENTED_SPIDER_EYE, key(Potions.SLOWNESS))
            builder.registerPotionRecipe(key(Potions.LONG_LEAPING), Items.FERMENTED_SPIDER_EYE, key(Potions.LONG_SLOWNESS))
            builder.registerPotionRecipe(key(Potions.SLOWNESS), Items.REDSTONE, key(Potions.LONG_SLOWNESS))
            builder.registerPotionRecipe(key(Potions.SLOWNESS), Items.GLOWSTONE_DUST, key(Potions.STRONG_SLOWNESS))

            builder.registerPotionRecipe(key(Potions.AWKWARD), Items.TURTLE_HELMET, key(Potions.TURTLE_MASTER))
            builder.registerPotionRecipe(key(Potions.TURTLE_MASTER), Items.REDSTONE, key(Potions.LONG_TURTLE_MASTER))
            builder.registerPotionRecipe(key(Potions.TURTLE_MASTER), Items.GLOWSTONE_DUST, key(Potions.STRONG_TURTLE_MASTER))

            builder.registerPotionRecipe(key(Potions.SWIFTNESS), Items.FERMENTED_SPIDER_EYE, key(Potions.SLOWNESS))
            builder.registerPotionRecipe(key(Potions.LONG_SWIFTNESS), Items.FERMENTED_SPIDER_EYE, key(Potions.LONG_SLOWNESS))

            builder.registerRecipes(Items.SUGAR, key(Potions.SWIFTNESS))
            builder.registerPotionRecipe(key(Potions.SWIFTNESS), Items.REDSTONE, key(Potions.LONG_SWIFTNESS))
            builder.registerPotionRecipe(key(Potions.SWIFTNESS), Items.GLOWSTONE_DUST, key(Potions.STRONG_SWIFTNESS))

            builder.registerPotionRecipe(key(Potions.AWKWARD), Items.PUFFERFISH, key(Potions.WATER_BREATHING))
            builder.registerPotionRecipe(key(Potions.WATER_BREATHING), Items.REDSTONE, key(Potions.LONG_WATER_BREATHING))

            builder.registerRecipes(Items.GLISTERING_MELON_SLICE, key(Potions.HEALING))
            builder.registerPotionRecipe(key(Potions.HEALING), Items.GLOWSTONE_DUST, key(Potions.STRONG_HEALING))
            builder.registerPotionRecipe(key(Potions.HEALING), Items.FERMENTED_SPIDER_EYE, key(Potions.HARMING))
            builder.registerPotionRecipe(key(Potions.STRONG_HEALING), Items.FERMENTED_SPIDER_EYE, key(Potions.STRONG_HARMING))
            builder.registerPotionRecipe(key(Potions.HARMING), Items.GLOWSTONE_DUST, key(Potions.STRONG_HARMING))

            builder.registerPotionRecipe(key(Potions.POISON), Items.FERMENTED_SPIDER_EYE, key(Potions.HARMING))
            builder.registerPotionRecipe(key(Potions.LONG_POISON), Items.FERMENTED_SPIDER_EYE, key(Potions.HARMING))
            builder.registerPotionRecipe(key(Potions.STRONG_POISON), Items.FERMENTED_SPIDER_EYE, key(Potions.STRONG_HARMING))

            builder.registerRecipes(Items.SPIDER_EYE, key(Potions.POISON))
            builder.registerPotionRecipe(key(Potions.POISON), Items.REDSTONE, key(Potions.LONG_POISON))
            builder.registerPotionRecipe(key(Potions.POISON), Items.GLOWSTONE_DUST, key(Potions.STRONG_POISON))

            builder.registerRecipes(Items.GHAST_TEAR, key(Potions.REGENERATION))
            builder.registerPotionRecipe(key(Potions.REGENERATION), Items.REDSTONE, key(Potions.LONG_REGENERATION))
            builder.registerPotionRecipe(key(Potions.REGENERATION), Items.GLOWSTONE_DUST, key(Potions.STRONG_REGENERATION))

            builder.registerRecipes(Items.BLAZE_POWDER, key(Potions.STRENGTH))
            builder.registerPotionRecipe(key(Potions.STRENGTH), Items.REDSTONE, key(Potions.LONG_STRENGTH))
            builder.registerPotionRecipe(key(Potions.STRENGTH), Items.GLOWSTONE_DUST, key(Potions.STRONG_STRENGTH))

            builder.registerPotionRecipe(key(Potions.WATER), Items.FERMENTED_SPIDER_EYE, key(Potions.WEAKNESS))
            builder.registerPotionRecipe(key(Potions.WEAKNESS), Items.REDSTONE, key(Potions.LONG_WEAKNESS))

            builder.registerPotionRecipe(key(Potions.AWKWARD), Items.PHANTOM_MEMBRANE, key(Potions.SLOW_FALLING))
            builder.registerPotionRecipe(key(Potions.SLOW_FALLING), Items.REDSTONE, key(Potions.LONG_SLOW_FALLING))
        }
    }
}