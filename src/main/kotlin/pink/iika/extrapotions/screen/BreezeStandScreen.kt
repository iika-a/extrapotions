package pink.iika.extrapotions.screen

import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import pink.iika.extrapotions.ExtraPotions

class BreezeStandScreen(handler: BreezeStandScreenHandler?, inventory: PlayerInventory?, title: Text?) : HandledScreen<BreezeStandScreenHandler?>(handler, inventory, title) {
    override fun drawBackground(
        context: DrawContext,
        delta: Float,
        mouseX: Int,
        mouseY: Int
    ) {
        val x = (width - backgroundWidth) / 2
        val y = (height - backgroundHeight) / 2

        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            x,
            y,
            0f,
            0f,
            backgroundWidth,
            backgroundHeight,
            256,
            256
        )
    }



    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context, mouseX, mouseY, delta)
        super.render(context, mouseX, mouseY, delta)
        drawMouseoverTooltip(context, mouseX, mouseY)
    }

    override fun init() {
        super.init()
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2
    }

    companion object {
        private val TEXTURE: Identifier = Identifier.of(ExtraPotions.MOD_ID, "textures/gui/container/breeze_stand.png")
    }
}