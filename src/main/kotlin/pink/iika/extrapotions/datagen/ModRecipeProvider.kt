package pink.iika.extrapotions.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.item.Items
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.util.Identifier
import pink.iika.extrapotions.ExtraPotions
import pink.iika.extrapotions.block.ModBlocks
import pink.iika.extrapotions.item.ModItems
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<WrapperLookup>
) : FabricRecipeProvider(output, registriesFuture) {
    override fun getRecipeGenerator(
        wrapperLookup: WrapperLookup,
        recipeExporter: RecipeExporter
    ): RecipeGenerator {
        return object : RecipeGenerator(wrapperLookup, recipeExporter) {
            override fun generate() {
                createShapeless(RecipeCategory.DECORATIONS, Items.WARPED_WART_BLOCK)
                    .input(ModItems.WARPED_WART)
                    .input(ModItems.WARPED_WART)
                    .input(ModItems.WARPED_WART)
                    .input(ModItems.WARPED_WART)
                    .input(ModItems.WARPED_WART)
                    .input(ModItems.WARPED_WART)
                    .input(ModItems.WARPED_WART)
                    .input(ModItems.WARPED_WART)
                    .input(ModItems.WARPED_WART)
                    .criterion(hasItem(ModItems.WARPED_WART), this.conditionsFromItem(ModItems.WARPED_WART))
                    .offerTo(recipeExporter)

                createShaped(RecipeCategory.BREWING, ModItems.AMETHYST_BOTTLE, 3)
                    .pattern("A A")
                    .pattern(" A ")
                    .input('A', Items.AMETHYST_SHARD)
                    .criterion(hasItem(Items.AMETHYST_SHARD), conditionsFromItem(Items.AMETHYST_SHARD))
                    .offerTo(recipeExporter)

                createShaped(RecipeCategory.BREWING, ModItems.BREEZE_POWDER, 2)
                    .pattern("PRP")
                    .input('P', Items.BLAZE_POWDER)
                    .input('R', Items.BREEZE_ROD)
                    .criterion(hasItem(Items.BLAZE_POWDER), conditionsFromItem(Items.BLAZE_POWDER))
                    .criterion(hasItem(Items.BREEZE_ROD), conditionsFromItem(Items.BREEZE_ROD))
                    .offerTo(recipeExporter)

                createShaped(RecipeCategory.BREWING, ModBlocks.BREEZING_STAND)
                    .pattern(" R ")
                    .pattern("III")
                    .input('I', Items.IRON_INGOT)
                    .input('R', Items.BREEZE_ROD)
                    .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                    .criterion(hasItem(Items.BREEZE_ROD), conditionsFromItem(Items.BREEZE_ROD))
                    .offerTo(recipeExporter)
            }
        }
    }

    override fun getName(): String {
        return Identifier.of(ExtraPotions.MOD_ID, "recipe_provider").toString()
    }
}