package dev.jaronline.cuttingdelight.forge.data;

import dev.jaronline.cuttingdelight.common.data.BaseBlockLoot;
import net.minecraft.world.level.block.Block;

public class BlockLoot extends BaseBlockLoot {
	@Override
	protected Iterable<Block> getKnownBlocks() {
		return generatedLootTables;
	}
}
