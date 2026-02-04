package pink.iika.extrapotions.block.entity

import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import pink.iika.extrapotions.ExtraPotions
import pink.iika.extrapotions.block.ModBlocks

object ModBlockEntities {
    @JvmStatic
    val BREEZE_STAND_BLOCK_ENTITY_TYPE: BlockEntityType<BreezeStandBlockEntity> =
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of(ExtraPotions.MOD_ID, "breeze_stand"),
            FabricBlockEntityTypeBuilder.create(
                ::BreezeStandBlockEntity,
                ModBlocks.BREEZE_STAND!!
            ).build()
        )

    fun registerModBlockEntities() {
        ExtraPotions.logger.info("Registering Block Entities for " + ExtraPotions.MOD_ID)
    }
}