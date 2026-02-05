package pink.iika.extrapotions.screen

import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import pink.iika.extrapotions.ExtraPotions

object ModScreenHandlers {
    @JvmStatic
    val BREEZING_STAND_BLOCK_SCREEN_HANDLER_TYPE: ScreenHandlerType<BreezingStandScreenHandler> =
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(ExtraPotions.MOD_ID, "breezing_stand_screen_handler"),
            ScreenHandlerType(::BreezingStandScreenHandler, FeatureSet.empty())
        )



    fun registerModScreenHandlers() {
        ExtraPotions.logger.info("Registering Screen Handlers for " + ExtraPotions.MOD_ID)
    }
}