package pink.iika.extrapotions.datagen

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.*
import net.minecraft.state.property.Properties
import pink.iika.extrapotions.block.ModBlocks
import pink.iika.extrapotions.item.ModItems


class ModModelProvider(output: FabricDataOutput): FabricModelProvider(output) {
    override fun generateBlockStateModels(gen: BlockStateModelGenerator) {
        gen.registerItemModel(ModBlocks.BREEZE_STAND)
        gen.blockStateCollector.accept(
            MultipartBlockModelDefinitionCreator.create(ModBlocks.BREEZE_STAND)
                .with(BlockStateModelGenerator.createWeightedVariant(TextureMap.getId(ModBlocks.BREEZE_STAND))).with(
                    BlockStateModelGenerator.createMultipartConditionBuilder()
                        .put(Properties.HAS_BOTTLE_0, true),
                    BlockStateModelGenerator.createWeightedVariant(
                        TextureMap.getSubId(
                            ModBlocks.BREEZE_STAND,
                            "_bottle0"
                        )
                    )
                ).with(
                    BlockStateModelGenerator.createMultipartConditionBuilder()
                        .put(Properties.HAS_BOTTLE_1, true),
                    BlockStateModelGenerator.createWeightedVariant(
                        TextureMap.getSubId(
                            ModBlocks.BREEZE_STAND,
                            "_bottle1"
                        )
                    )
                ).with(
                    BlockStateModelGenerator.createMultipartConditionBuilder()
                        .put(Properties.HAS_BOTTLE_2, true),
                    BlockStateModelGenerator.createWeightedVariant(
                        TextureMap.getSubId(
                            ModBlocks.BREEZE_STAND,
                            "_bottle2"
                        )
                    )
                ).with(
                    BlockStateModelGenerator.createMultipartConditionBuilder()
                        .put(Properties.HAS_BOTTLE_0, false),
                    BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(ModBlocks.BREEZE_STAND, "_empty0"))
                ).with(
                    BlockStateModelGenerator.createMultipartConditionBuilder()
                        .put(Properties.HAS_BOTTLE_1, false),
                    BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(ModBlocks.BREEZE_STAND, "_empty1"))
                ).with(
                    BlockStateModelGenerator.createMultipartConditionBuilder()
                        .put(Properties.HAS_BOTTLE_2, false),
                    BlockStateModelGenerator.createWeightedVariant(TextureMap.getSubId(ModBlocks.BREEZE_STAND, "_empty2"))
                )
        )
    }

    override fun generateItemModels(gen: ItemModelGenerator) {
        gen.register(ModItems.BREEZE_POWDER, Models.GENERATED)
        gen.register(ModItems.AMETHYST_BOTTLE, Models.GENERATED)
        gen.register(ModItems.WARPED_WART, Models.GENERATED)
    }
}