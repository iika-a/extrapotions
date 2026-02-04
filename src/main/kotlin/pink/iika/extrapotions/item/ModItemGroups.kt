package pink.iika.extrapotions.item

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import pink.iika.extrapotions.ExtraPotions

object ModItemGroups {

    lateinit var EXTRA_POTIONS_GROUP: ItemGroup

    fun registerModItemGroups() {
        ExtraPotions.logger.info("Registering Item Groups for ${ExtraPotions.MOD_ID}")

        EXTRA_POTIONS_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(ExtraPotions.MOD_ID, "kitium_items"),
            FabricItemGroup.builder()
                .icon { ItemStack(ModItems.AMETHYST_BOTTLE) }
                .displayName(Text.translatable("itemgroup.extrapotions.extrapotions_items"))
                .entries { displayContext, entries ->
                    entries.add(ModItems.WARPED_WART)
                    entries.add(ModItems.BREEZE_POWDER)
                    entries.add(ModItems.AMETHYST_BOTTLE)
                }
                .build()
        )
    }
}
