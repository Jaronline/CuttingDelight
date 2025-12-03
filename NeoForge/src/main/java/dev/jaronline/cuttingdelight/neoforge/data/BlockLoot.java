package dev.jaronline.cuttingdelight.neoforge.data;

import dev.jaronline.cuttingdelight.common.data.BaseBlockLoot;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

public class BlockLoot extends BaseBlockLoot {
    protected BlockLoot(HolderLookup.Provider registries) {
        super(registries);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return generatedLootTables;
    }
}
