package pink.iika.extrapotions.datagen

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.*
import net.minecraft.state.property.Properties
import pink.iika.extrapotions.block.ModBlocks
import pink.iika.extrapotions.item.ModItems


class ModModelProvider(output: FabricDataOutput): FabricModelProvider(output) {
    override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
       //nothing yet
    }

    override fun generateItemModels(gen: ItemModelGenerator) {
        gen.register(ModItems.BREEZE_POWDER, Models.GENERATED)
        gen.register(ModItems.AMETHYST_BOTTLE, Models.GENERATED)
        gen.register(ModItems.WARPED_WART, Models.GENERATED)
        gen.registerPotion(ModItems.AMETHYST_POTION)
        gen.registerPotion(ModItems.AMETHYST_SPLASH_POTION)
        gen.registerPotion(ModItems.AMETHYST_LINGERING_POTION)
    }
}