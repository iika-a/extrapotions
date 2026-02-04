package pink.iika.extrapotions.block

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.Identifier
import pink.iika.extrapotions.ExtraPotions

object ModBlocks {
    @JvmStatic
    val BREEZE_STAND: Block? = registerBlock(
        "breeze_stand", BreezeStandBlock(
            AbstractBlock.Settings.create()
                .registryKey(
                    RegistryKey.of(
                        RegistryKeys.BLOCK,
                        Identifier.of(ExtraPotions.MOD_ID, "breeze_stand")
                    )
                )
                .strength(4f).requiresTool().sounds(BlockSoundGroup.STONE)
        )
    )

    private fun registerBlock(name: String, block: Block?): Block? {
        registerBlockItem(name, block)
        return Registry.register<Block?, Block?>(Registries.BLOCK, Identifier.of(ExtraPotions.MOD_ID, name), block)
    }

    private fun registerBlockItem(name: String, block: Block?) {
        Registry.register(
            Registries.ITEM, Identifier.of(ExtraPotions.MOD_ID, name), BlockItem(
                block, Item.Settings()
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ExtraPotions.MOD_ID, name)))
                    .useBlockPrefixedTranslationKey()
            )
        )
    }

    fun registerModBlocks() {
        ExtraPotions.logger.info("Registering Mod Blocks for" + ExtraPotions.MOD_ID)

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ItemGroupEvents.ModifyEntries { entries: FabricItemGroupEntries ->
            entries.add(BREEZE_STAND)
        })
    }
}