package pink.iika.extrapotions

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pink.iika.extrapotions.block.ModBlocks
import pink.iika.extrapotions.block.entity.ModBlockEntities
import pink.iika.extrapotions.item.ModItemGroups
import pink.iika.extrapotions.item.ModItems

object ExtraPotions : ModInitializer {
	const val MOD_ID = "extrapotions"
    @JvmField val logger: Logger = LoggerFactory.getLogger("extrapotions")

	override fun onInitialize() {
		logger.info("Initializing Extra Potions...")
		ModItems.registerModItems()
		ModItemGroups.registerModItemGroups()
		ModBlocks.registerModBlocks()
		ModBlockEntities.registerModBlockEntities()
	}
}