@file:Mixin(InGameHud::class)

package pink.iika.extrapotions.mixin

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyVariable
import pink.iika.extrapotions.ExtraPotions
import pink.iika.extrapotions.entity.effect.ModStatusEffects

val FOOD_EMPTY_NOURISHMENT_TEXTURE: Identifier = Identifier.of(ExtraPotions.MOD_ID, "hud/food_empty")

@ModifyVariable(
    method = ["renderFood(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;II)V"],
    at = At("STORE"),
    ordinal = 0
) internal fun replaceEmptyTexture(original: Identifier,
      context: DrawContext,
      player: PlayerEntity,
      top: Int,
      right: Int): Identifier {
    return if (player.hasStatusEffect(ModStatusEffects.NOURISHMENT)) FOOD_EMPTY_NOURISHMENT_TEXTURE else original
}

@ModifyVariable(
    method = ["renderFood"],
    at = At("STORE"),
    ordinal = 3
) internal fun replaceEmptyHungerTexture(original: Identifier,
                          context: DrawContext,
                          player: PlayerEntity,
                          top: Int,
                          right: Int): Identifier {
    return if (player.hasStatusEffect(ModStatusEffects.NOURISHMENT)) FOOD_EMPTY_NOURISHMENT_TEXTURE else original
}