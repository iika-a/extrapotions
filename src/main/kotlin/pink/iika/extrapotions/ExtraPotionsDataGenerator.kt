package pink.iika.extrapotions

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import pink.iika.extrapotions.datagen.ModModelProvider
import pink.iika.extrapotions.datagen.ModRecipeProvider

object ExtraPotionsDataGenerator : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()

		pack.addProvider(::ModRecipeProvider)
		pack.addProvider(::ModModelProvider)
	}
}