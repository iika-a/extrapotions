package pink.iika.extrapotions.item

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import pink.iika.extrapotions.ExtraPotions

object ModItems {
    @JvmStatic
    val BREEZE_POWDER: Item = registerItem(
        "breeze_powder", Item(
            Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ExtraPotions.MOD_ID, "breeze_powder")))
        )
    )
    @JvmStatic
    val AMETHYST_BOTTLE: Item = registerItem(
        "amethyst_bottle", Item(
            Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ExtraPotions.MOD_ID, "amethyst_bottle")))
        )
    )
    @JvmStatic
    val WARPED_WART: Item = registerItem(
        "warped_wart", Item(
            Item.Settings()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ExtraPotions.MOD_ID, "warped_wart")))
        )
    )

    private fun registerItem(name: String, item: Item): Item {
        return Registry.register(Registries.ITEM, Identifier.of(ExtraPotions.MOD_ID, name), item)
    }

    fun registerModItems() {
        ExtraPotions.logger.info("Registering Items for " + ExtraPotions.MOD_ID)

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(ModifyEntries { entries: FabricItemGroupEntries ->
            entries.add(BREEZE_POWDER)
            entries.add(AMETHYST_BOTTLE)
            entries.add(WARPED_WART)
        })
    }
}