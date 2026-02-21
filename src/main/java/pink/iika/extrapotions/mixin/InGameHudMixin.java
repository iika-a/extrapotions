package pink.iika.extrapotions.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import pink.iika.extrapotions.ExtraPotions;
import pink.iika.extrapotions.entity.effect.ModStatusEffects;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Unique
    private static final Identifier FOOD_EMPTY_NOURISHMENT_TEXTURE =
            Identifier.of(ExtraPotions.MOD_ID, "hud/food_empty_nourishment");
    @Unique
    private static final Identifier FOOD_HALF_NOURISHMENT_TEXTURE =
            Identifier.of(ExtraPotions.MOD_ID, "hud/food_half_nourishment");
    @Unique
    private static final Identifier FOOD_FULL_NOURISHMENT_TEXTURE =
            Identifier.of(ExtraPotions.MOD_ID, "hud/food_full_nourishment");

    @ModifyVariable(
            method = "renderFood",
            at = @At("STORE"),
            ordinal = 0
    )
    private Identifier replaceEmptyTexture(
            Identifier original,
            DrawContext context,
            PlayerEntity player,
            int top,
            int right
    ) {
        return player.hasStatusEffect(ModStatusEffects.NOURISHMENT)
                ? FOOD_EMPTY_NOURISHMENT_TEXTURE
                : original;
    }

    @ModifyVariable(
            method = "renderFood",
            at = @At("STORE"),
            ordinal = 1
    )
    private Identifier replaceHalfTexture(
            Identifier original,
            DrawContext context,
            PlayerEntity player,
            int top,
            int right
    ) {
        return player.hasStatusEffect(ModStatusEffects.NOURISHMENT)
                ? FOOD_HALF_NOURISHMENT_TEXTURE
                : original;
    }

    @ModifyVariable(
            method = "renderFood",
            at = @At("STORE"),
            ordinal = 2
    )
    private Identifier replaceFullTexture(
            Identifier original,
            DrawContext context,
            PlayerEntity player,
            int top,
            int right
    ) {
        return player.hasStatusEffect(ModStatusEffects.NOURISHMENT)
                ? FOOD_FULL_NOURISHMENT_TEXTURE
                : original;
    }
}