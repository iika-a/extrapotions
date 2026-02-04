package pink.iika.extrapotions

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.render.BlockRenderLayer
import pink.iika.extrapotions.block.ModBlocks
import pink.iika.extrapotions.screen.BreezeStandScreen
import pink.iika.extrapotions.screen.ModScreenHandlers

@Environment(EnvType.CLIENT)
object ExtraPotionsClient: ClientModInitializer {
    override fun onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.BREEZE_STAND_BLOCK_SCREEN_HANDLER_TYPE, ::BreezeStandScreen)
        ModScreenHandlers.registerModScreenHandlers()
        BlockRenderLayerMap.putBlock(ModBlocks.BREEZE_STAND!!, BlockRenderLayer.CUTOUT)
    }
}