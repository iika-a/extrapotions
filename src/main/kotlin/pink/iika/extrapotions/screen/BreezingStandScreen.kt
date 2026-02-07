package pink.iika.extrapotions.screen

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import pink.iika.extrapotions.ExtraPotions

@Environment(EnvType.CLIENT)
class BreezingStandScreen(handler: BreezingStandScreenHandler?, inventory: PlayerInventory, title: Text?) :
    HandledScreen<BreezingStandScreenHandler?>(handler, inventory, title) {
    override fun init() {
        super.init()
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        super.render(context, mouseX, mouseY, deltaTicks)
        this.drawMouseoverTooltip(context, mouseX, mouseY)
    }

    override fun drawBackground(context: DrawContext, deltaTicks: Float, mouseX: Int, mouseY: Int) {
        val i = (this.width - this.backgroundWidth) / 2
        val j = (this.height - this.backgroundHeight) / 2
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            i,
            j,
            0.0f,
            0.0f,
            this.backgroundWidth,
            this.backgroundHeight,
            256,
            256
        )
        val k = (this.handler as BreezingStandScreenHandler).fuel
        val l = MathHelper.clamp((18 * k + 20 - 1) / 20, 0, 18)
        if (l > 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, FUEL_LENGTH_TEXTURE, 4, 18, 0, 0, i + 8, j + 50, 4, l)
        }

        val m = (this.handler as BreezingStandScreenHandler).brewTime
        if (m > 0) {
            var n = (28.0f * (1.0f - m.toFloat() / 400.0f)).toInt()
            if (n > 0) {
                context.drawGuiTexture(
                    RenderPipelines.GUI_TEXTURED,
                    BREW_PROGRESS_TEXTURE,
                    9,
                    28,
                    0,
                    0,
                    i + 107,
                    j + 18,
                    9,
                    n
                )
            }

            n = CURVES_PROGRESS[m / 2 % 7]
            if (n > 0) {
                context.drawGuiTexture(
                    RenderPipelines.GUI_TEXTURED,
                    WIND_CURVES_TEXTURE,
                    39,
                    19,
                    0,
                    0,
                    i + 15,
                    j + 50,
                    n,
                    19
                )
            }
        }
    }

    companion object {
        private val FUEL_LENGTH_TEXTURE: Identifier = Identifier.of(ExtraPotions.MOD_ID, "container/breezing_stand/fuel_length2")
        private val BREW_PROGRESS_TEXTURE: Identifier = Identifier.of(ExtraPotions.MOD_ID, "container/breezing_stand/brew_progress")
        private val WIND_CURVES_TEXTURE: Identifier = Identifier.of(ExtraPotions.MOD_ID, "container/breezing_stand/wind_curves")
        private val TEXTURE: Identifier = Identifier.of(ExtraPotions.MOD_ID, "textures/gui/container/breezing_stand.png")
        private val CURVES_PROGRESS = intArrayOf(39, 33, 28, 21, 14, 9, 0)
    }
}