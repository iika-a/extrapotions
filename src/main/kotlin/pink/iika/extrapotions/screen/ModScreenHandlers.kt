package pink.iika.extrapotions.screen

import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.resource.featuretoggle.FeatureSet
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier
import pink.iika.extrapotions.ExtraPotions

object ModScreenHandlers {
    @JvmStatic
    val BREEZE_STAND_BLOCK_SCREEN_HANDLER_TYPE: ScreenHandlerType<BreezeStandScreenHandler> =
        Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(ExtraPotions.MOD_ID, "lightning_collector_screen_handler"),
            ScreenHandlerType(::BreezeStandScreenHandler, FeatureSet.empty())
        )



    fun registerModScreenHandlers() {
        ExtraPotions.logger.info("Registering Screen Handlers for " + ExtraPotions.MOD_ID)
    }
}