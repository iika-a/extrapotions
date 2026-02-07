package pink.iika.extrapotions.potion

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemGroups
import net.minecraft.potion.Potion
import net.minecraft.potion.Potions
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.Identifier
import pink.iika.extrapotions.ExtraPotions
import pink.iika.extrapotions.block.ModBlocks.BREEZING_STAND

object ModPotions {

    private fun register(name: String, potion: Potion): RegistryEntry<Potion> {
        return Registry.registerReference(
            Registries.POTION,
            Identifier.of(ExtraPotions.MOD_ID, name),
            potion
        )
    }

    @JvmField
    val GALE = register("gale", Potion("gale"))

    @JvmField
    val GLOWING = register(
        "glowing",
        Potion(
            "glowing",
            StatusEffectInstance(StatusEffects.GLOWING, 3600)
        )
    )
    @JvmField
    val LONG_GLOWING = register(
        "long_glowing",
        Potion(
            "glowing",
            StatusEffectInstance(StatusEffects.GLOWING, 9600)
        )
    )

    @JvmField
    val ABSORPTION = register(
        "absorption",
        Potion(
            "absorption",
            StatusEffectInstance(StatusEffects.ABSORPTION, 3600)
        )
    )
    @JvmField
    val LONG_ABSORPTION = register(
        "long_absorption",
        Potion(
            "absorption",
            StatusEffectInstance(StatusEffects.ABSORPTION, 9600)
        )
    )
    @JvmField
    val STRONG_ABSORPTION = register(
        "strong_absorption",
        Potion(
            "absorption",
            StatusEffectInstance(StatusEffects.ABSORPTION, 1800, 1)
        )
    )

    @JvmField
    val LEVITATION = register(
        "levitation",
        Potion(
            "levitation",
            StatusEffectInstance(StatusEffects.LEVITATION, 150)
        )
    )
    @JvmField
    val LONG_LEVITATION = register(
        "long_levitation",
        Potion(
            "levitation",
            StatusEffectInstance(StatusEffects.LEVITATION, 200)
        )
    )
    @JvmField
    val STRONG_LEVITATION = register(
        "strong_levitation",
        Potion(
            "levitation",
            StatusEffectInstance(StatusEffects.LEVITATION, 100, 1)
        )
    )

    @JvmField
    val LONG_LUCK = register(
        "long_luck",
        Potion(
            "luck",
            StatusEffectInstance(StatusEffects.LUCK, 12000)
        )
    )
    @JvmField
    val STRONG_LUCK = register(
        "strong_luck",
        Potion(
            "luck",
            StatusEffectInstance(StatusEffects.LUCK, 4800, 1)
        )
    )

    @JvmField
    val UNLUCK = register(
        "unluck",
        Potion(
            "unluck",
            StatusEffectInstance(StatusEffects.UNLUCK, 6000)
        )
    )
    @JvmField
    val LONG_UNLUCK = register(
        "long_unluck",
        Potion(
            "unluck",
            StatusEffectInstance(StatusEffects.UNLUCK, 12000)
        )
    )
    @JvmField
    val STRONG_UNLUCK = register(
        "strong_unluck",
        Potion(
            "unluck",
            StatusEffectInstance(StatusEffects.UNLUCK, 4800, 1)
        )
    )

    /*
    @JvmField
    val NIGHT_VISION = register(
        "night_vision",
        Potion(
            "night_vision",
            StatusEffectInstance(StatusEffects.NIGHT_VISION, 3600)
        )
    )

    @JvmField
    val LONG_NIGHT_VISION = register(
        "long_night_vision",
        Potion(
            "night_vision",
            StatusEffectInstance(StatusEffects.NIGHT_VISION, 9600)
        )
    )

    @JvmField
    val INVISIBILITY = register(
        "invisibility",
        Potion(
            "invisibility",
            StatusEffectInstance(StatusEffects.INVISIBILITY, 3600)
        )
    )

    @JvmField
    val LONG_INVISIBILITY = register(
        "long_invisibility",
        Potion(
            "invisibility",
            StatusEffectInstance(StatusEffects.INVISIBILITY, 9600)
        )
    )

    @JvmField
    val LEAPING = register(
        "leaping",
        Potion(
            "leaping",
            StatusEffectInstance(StatusEffects.JUMP_BOOST, 3600)
        )
    )

    @JvmField
    val LONG_LEAPING = register(
        "long_leaping",
        Potion(
            "leaping",
            StatusEffectInstance(StatusEffects.JUMP_BOOST, 9600)
        )
    )

    @JvmField
    val STRONG_LEAPING = register(
        "strong_leaping",
        Potion(
            "leaping",
            StatusEffectInstance(StatusEffects.JUMP_BOOST, 1800, 1)
        )
    )

    @JvmField
    val FIRE_RESISTANCE = register(
        "fire_resistance",
        Potion(
            "fire_resistance",
            StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 3600)
        )
    )

    @JvmField
    val LONG_FIRE_RESISTANCE = register(
        "long_fire_resistance",
        Potion(
            "fire_resistance",
            StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 9600)
        )
    )

    @JvmField
    val SWIFTNESS = register(
        "swiftness",
        Potion(
            "swiftness",
            StatusEffectInstance(StatusEffects.SPEED, 3600)
        )
    )

    @JvmField
    val LONG_SWIFTNESS = register(
        "long_swiftness",
        Potion(
            "swiftness",
            StatusEffectInstance(StatusEffects.SPEED, 9600)
        )
    )

    @JvmField
    val STRONG_SWIFTNESS = register(
        "strong_swiftness",
        Potion(
            "swiftness",
            StatusEffectInstance(StatusEffects.SPEED, 1800, 1)
        )
    )

    @JvmField
    val SLOWNESS = register(
        "slowness",
        Potion(
            "slowness",
            StatusEffectInstance(StatusEffects.SLOWNESS, 1800)
        )
    )

    @JvmField
    val LONG_SLOWNESS = register(
        "long_slowness",
        Potion(
            "slowness",
            StatusEffectInstance(StatusEffects.SLOWNESS, 4800)
        )
    )

    @JvmField
    val STRONG_SLOWNESS = register(
        "strong_slowness",
        Potion(
            "slowness",
            StatusEffectInstance(StatusEffects.SLOWNESS, 400, 3)
        )
    )

    @JvmField
    val TURTLE_MASTER = register(
        "turtle_master",
        Potion(
            "turtle_master",
            StatusEffectInstance(StatusEffects.SLOWNESS, 400, 3),
            StatusEffectInstance(StatusEffects.RESISTANCE, 400, 2)
        )
    )

    @JvmField
    val LONG_TURTLE_MASTER = register(
        "long_turtle_master",
        Potion(
            "turtle_master",
            StatusEffectInstance(StatusEffects.SLOWNESS, 800, 3),
            StatusEffectInstance(StatusEffects.RESISTANCE, 800, 2)
        )
    )

    @JvmField
    val STRONG_TURTLE_MASTER = register(
        "strong_turtle_master",
        Potion(
            "turtle_master",
            StatusEffectInstance(StatusEffects.SLOWNESS, 400, 5),
            StatusEffectInstance(StatusEffects.RESISTANCE, 400, 3)
        )
    )

    @JvmField
    val WATER_BREATHING = register(
        "water_breathing",
        Potion(
            "water_breathing",
            StatusEffectInstance(StatusEffects.WATER_BREATHING, 3600)
        )
    )

    @JvmField
    val LONG_WATER_BREATHING = register(
        "long_water_breathing",
        Potion(
            "water_breathing",
            StatusEffectInstance(StatusEffects.WATER_BREATHING, 9600)
        )
    )

    @JvmField
    val HEALING = register(
        "healing",
        Potion(
            "healing",
            StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1)
        )
    )

    @JvmField
    val STRONG_HEALING = register(
        "strong_healing",
        Potion(
            "healing",
            StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 1)
        )
    )

    @JvmField
    val HARMING = register(
        "harming",
        Potion(
            "harming",
            StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1)
        )
    )

    @JvmField
    val STRONG_HARMING = register(
        "strong_harming",
        Potion(
            "harming",
            StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 1)
        )
    )

    @JvmField
    val POISON = register(
        "poison",
        Potion(
            "poison",
            StatusEffectInstance(StatusEffects.POISON, 900)
        )
    )

    @JvmField
    val LONG_POISON = register(
        "long_poison",
        Potion(
            "poison",
            StatusEffectInstance(StatusEffects.POISON, 1800)
        )
    )

    @JvmField
    val STRONG_POISON = register(
        "strong_poison",
        Potion(
            "poison",
            StatusEffectInstance(StatusEffects.POISON, 432, 1)
        )
    )

    @JvmField
    val REGENERATION = register(
        "regeneration",
        Potion(
            "regeneration",
            StatusEffectInstance(StatusEffects.REGENERATION, 900)
        )
    )

    @JvmField
    val LONG_REGENERATION = register(
        "long_regeneration",
        Potion(
            "regeneration",
            StatusEffectInstance(StatusEffects.REGENERATION, 1800)
        )
    )

    @JvmField
    val STRONG_REGENERATION = register(
        "strong_regeneration",
        Potion(
            "regeneration",
            StatusEffectInstance(StatusEffects.REGENERATION, 450, 1)
        )
    )

    @JvmField
    val STRENGTH = register(
        "strength",
        Potion(
            "strength",
            StatusEffectInstance(StatusEffects.STRENGTH, 3600)
        )
    )

    @JvmField
    val LONG_STRENGTH = register(
        "long_strength",
        Potion(
            "strength",
            StatusEffectInstance(StatusEffects.STRENGTH, 9600)
        )
    )

    @JvmField
    val STRONG_STRENGTH = register(
        "strong_strength",
        Potion(
            "strength",
            StatusEffectInstance(StatusEffects.STRENGTH, 1800, 1)
        )
    )

    @JvmField
    val WEAKNESS = register(
        "weakness",
        Potion(
            "weakness",
            StatusEffectInstance(StatusEffects.WEAKNESS, 1800)
        )
    )

    @JvmField
    val LONG_WEAKNESS = register(
        "long_weakness",
        Potion(
            "weakness",
            StatusEffectInstance(StatusEffects.WEAKNESS, 4800)
        )
    )

    @JvmField
    val LUCK = register(
        "luck",
        Potion(
            "luck",
            StatusEffectInstance(StatusEffects.LUCK, 6000)
        )
    )

    @JvmField
    val SLOW_FALLING = register(
        "slow_falling",
        Potion(
            "slow_falling",
            StatusEffectInstance(StatusEffects.SLOW_FALLING, 1800)
        )
    )

    @JvmField
    val LONG_SLOW_FALLING = register(
        "long_slow_falling",
        Potion(
            "slow_falling",
            StatusEffectInstance(StatusEffects.SLOW_FALLING, 4800)
        )
    )

    @JvmField
    val WIND_CHARGED = register(
        "wind_charged",
        Potion(
            "wind_charged",
            StatusEffectInstance(StatusEffects.WIND_CHARGED, 3600)
        )
    )

    @JvmField
    val WEAVING = register(
        "weaving",
        Potion(
            "weaving",
            StatusEffectInstance(StatusEffects.WEAVING, 3600)
        )
    )

    @JvmField
    val OOZING = register(
        "oozing",
        Potion(
            "oozing",
            StatusEffectInstance(StatusEffects.OOZING, 3600)
        )
    )

    @JvmField
    val INFESTED = register(
        "infested",
        Potion(
            "infested",
            StatusEffectInstance(StatusEffects.INFESTED, 3600)
        )
    )
    */

    fun registerAndGetDefault(): RegistryEntry<Potion> = Potions.WATER

    fun registerModPotions() {
        ExtraPotions.logger.info("Registering Mod Potions for" + ExtraPotions.MOD_ID)
    }
}
