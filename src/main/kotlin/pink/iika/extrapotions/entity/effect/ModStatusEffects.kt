package pink.iika.extrapotions.entity.effect

import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectCategory
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

object ModStatusEffects {

    //private const val DARKNESS_PADDING_DURATION = 22

    private fun register(id: String, effect: StatusEffect): RegistryEntry<StatusEffect> {
        return Registry.registerReference(
            Registries.STATUS_EFFECT,
            Identifier.ofVanilla(id),
            effect
        )
    }

    /*
    @JvmField
    val SPEED = register(
        "speed",
        ModStatusEffect(StatusEffectCategory.BENEFICIAL, 0x33EBFF)
            .addAttributeModifier(
                EntityAttributes.MOVEMENT_SPEED,
                Identifier.ofVanilla("effect.speed"),
                0.2,
                Operation.ADD_MULTIPLIED_TOTAL
            )
    )

    @JvmField
    val SLOWNESS = register(
        "slowness",
        ModStatusEffect(StatusEffectCategory.HARMFUL, 0x8BAFE0)
            .addAttributeModifier(
                EntityAttributes.MOVEMENT_SPEED,
                Identifier.ofVanilla("effect.slowness"),
                -0.15,
                Operation.ADD_MULTIPLIED_TOTAL
            )
    )

    @JvmField
    val HASTE = register(
        "haste",
        ModStatusEffect(StatusEffectCategory.BENEFICIAL, 0xD9C043)
            .addAttributeModifier(
                EntityAttributes.ATTACK_SPEED,
                Identifier.ofVanilla("effect.haste"),
                0.1,
                Operation.ADD_MULTIPLIED_TOTAL
            )
    )

    @JvmField
    val MINING_FATIGUE = register(
        "mining_fatigue",
        ModStatusEffect(StatusEffectCategory.HARMFUL, 0x4A4217)
            .addAttributeModifier(
                EntityAttributes.ATTACK_SPEED,
                Identifier.ofVanilla("effect.mining_fatigue"),
                -0.1,
                Operation.ADD_MULTIPLIED_TOTAL
            )
    )

    @JvmField
    val STRENGTH = register(
        "strength",
        ModStatusEffect(StatusEffectCategory.BENEFICIAL, 0xFF0000)
            .addAttributeModifier(
                EntityAttributes.ATTACK_DAMAGE,
                Identifier.ofVanilla("effect.strength"),
                3.0,
                Operation.ADD_VALUE
            )
    )

    @JvmField
    val INSTANT_HEALTH = register(
        "instant_health",
        InstantHealthOrDamageStatusEffect(
            StatusEffectCategory.BENEFICIAL,
            16262179,
            false
        )
    )

    @JvmField
    val INSTANT_DAMAGE = register(
        "instant_damage",
        InstantHealthOrDamageStatusEffect(
            StatusEffectCategory.HARMFUL,
            11101546,
            true
        )
    )

    @JvmField
    val JUMP_BOOST = register(
        "jump_boost",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 16646020)
            .addAttributeModifier(
                EntityAttributes.SAFE_FALL_DISTANCE,
                Identifier.ofVanilla("effect.jump_boost"),
                1.0,
                Operation.ADD_VALUE
            )
    )

    @JvmField
    val NAUSEA = register(
        "nausea",
        StatusEffect(StatusEffectCategory.HARMFUL, 5578058)
            .fadeTicks(150, 20, 60)
    )

    @JvmField
    val REGENERATION = register(
        "regeneration",
        RegenerationStatusEffect(
            StatusEffectCategory.BENEFICIAL,
            13458603
        )
    )

    @JvmField
    val RESISTANCE = register(
        "resistance",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 9520880)
    )

    @JvmField
    val FIRE_RESISTANCE = register(
        "fire_resistance",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 16750848)
    )

    @JvmField
    val WATER_BREATHING = register(
        "water_breathing",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 10017472)
    )

    @JvmField
    val INVISIBILITY = register(
        "invisibility",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 16185078)
            .addAttributeModifier(
                EntityAttributes.WAYPOINT_TRANSMIT_RANGE,
                Identifier.ofVanilla("effect.waypoint_transmit_range_hide"),
                -1.0,
                Operation.ADD_MULTIPLIED_TOTAL
            )
    )

    @JvmField
    val BLINDNESS = register(
        "blindness",
        StatusEffect(StatusEffectCategory.HARMFUL, 2039587)
    )

    @JvmField
    val NIGHT_VISION = register(
        "night_vision",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 12779366)
    )

    @JvmField
    val HUNGER = register(
        "hunger",
        HungerStatusEffect(
            StatusEffectCategory.HARMFUL,
            5797459
        )
    )

    @JvmField
    val WEAKNESS = register(
        "weakness",
        StatusEffect(StatusEffectCategory.HARMFUL, 4738376)
            .addAttributeModifier(
                EntityAttributes.ATTACK_DAMAGE,
                Identifier.ofVanilla("effect.weakness"),
                -4.0,
                Operation.ADD_VALUE
            )
    )

    @JvmField
    val POISON = register(
        "poison",
        PoisonStatusEffect(
            StatusEffectCategory.HARMFUL,
            8889187
        )
    )

    @JvmField
    val WITHER = register(
        "wither",
        WitherStatusEffect(
            StatusEffectCategory.HARMFUL,
            7561558
        )
    )

    @JvmField
    val HEALTH_BOOST = register(
        "health_boost",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 16284963)
            .addAttributeModifier(
                EntityAttributes.MAX_HEALTH,
                Identifier.ofVanilla("effect.health_boost"),
                4.0,
                Operation.ADD_VALUE
            )
    )

    @JvmField
    val ABSORPTION = register(
        "absorption",
        AbsorptionStatusEffect(StatusEffectCategory.BENEFICIAL, 2445989)
            .addAttributeModifier(
                EntityAttributes.MAX_ABSORPTION,
                Identifier.ofVanilla("effect.absorption"),
                4.0,
                Operation.ADD_VALUE
            )
    )

    @JvmField
    val SATURATION = register(
        "saturation",
        SaturationStatusEffect(
            StatusEffectCategory.BENEFICIAL,
            16262179
        )
    )

    @JvmField
    val GLOWING = register(
        "glowing",
        StatusEffect(StatusEffectCategory.NEUTRAL, 9740385)
    )

    @JvmField
    val LEVITATION = register(
        "levitation",
        StatusEffect(StatusEffectCategory.HARMFUL, 13565951)
    )

    @JvmField
    val LUCK = register(
        "luck",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 5882118)
            .addAttributeModifier(
                EntityAttributes.LUCK,
                Identifier.ofVanilla("effect.luck"),
                1.0,
                Operation.ADD_VALUE
            )
    )

    @JvmField
    val UNLUCK = register(
        "unluck",
        StatusEffect(StatusEffectCategory.HARMFUL, 12624973)
            .addAttributeModifier(
                EntityAttributes.LUCK,
                Identifier.ofVanilla("effect.unluck"),
                -1.0,
                Operation.ADD_VALUE
            )
    )

    @JvmField
    val SLOW_FALLING = register(
        "slow_falling",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 15978425)
    )

    @JvmField
    val CONDUIT_POWER = register(
        "conduit_power",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 1950417)
    )

    @JvmField
    val DOLPHINS_GRACE = register(
        "dolphins_grace",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 8954814)
    )

    @JvmField
    val BAD_OMEN = register(
        "bad_omen",
        BadOmenStatusEffect(StatusEffectCategory.NEUTRAL, 745784)
            .applySound(SoundEvents.EVENT_MOB_EFFECT_BAD_OMEN)
    )

    @JvmField
    val HERO_OF_THE_VILLAGE = register(
        "hero_of_the_village",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 4521796)
    )

    @JvmField
    val DARKNESS = register(
        "darkness",
        StatusEffect(StatusEffectCategory.HARMFUL, 2696993)
            .fadeTicks(DARKNESS_PADDING_DURATION)
    )

    @JvmField
    val TRIAL_OMEN = register(
        "trial_omen",
        StatusEffect(
            StatusEffectCategory.NEUTRAL,
            1484454,
            ParticleTypes.TRIAL_OMEN
        ).applySound(SoundEvents.EVENT_MOB_EFFECT_TRIAL_OMEN)
    )

    @JvmField
    val RAID_OMEN = register(
        "raid_omen",
        RaidOmenStatusEffect(
            StatusEffectCategory.NEUTRAL,
            14565464,
            ParticleTypes.RAID_OMEN
        ).applySound(SoundEvents.EVENT_MOB_EFFECT_RAID_OMEN)
    )

    @JvmField
    val WIND_CHARGED = register(
        "wind_charged",
        WindChargedStatusEffect(
            StatusEffectCategory.HARMFUL,
            12438015
        )
    )

    @JvmField
    val WEAVING = register(
        "weaving",
        WeavingStatusEffect(
            StatusEffectCategory.HARMFUL,
            7891290
        ) { random ->
            MathHelper.nextBetween(random, 2, 3)
        }
    )

    @JvmField
    val OOZING = register(
        "oozing",
        OozingStatusEffect(
            StatusEffectCategory.HARMFUL,
            10092451
        ) { 2 }
    )

    @JvmField
    val INFESTED = register(
        "infested",
        InfestedStatusEffect(
            StatusEffectCategory.HARMFUL,
            9214860,
            0.1f
        ) { random ->
            MathHelper.nextBetween(random, 1, 2)
        }
    )

    @JvmField
    val BREATH_OF_THE_NAUTILUS = register(
        "breath_of_the_nautilus",
        StatusEffect(StatusEffectCategory.BENEFICIAL, 65518)
    )

    fun registerAndGetDefault(): RegistryEntry<StatusEffect> = SPEED

     */
}
