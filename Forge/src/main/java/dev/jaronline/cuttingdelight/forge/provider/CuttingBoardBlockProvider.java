package dev.jaronline.cuttingdelight.forge.provider;

import dev.jaronline.cuttingdelight.common.provider.SimpleObjectProvider;
import dev.jaronline.cuttingdelight.core.provider.AutoProvider;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;

@AutoProvider
public class CuttingBoardBlockProvider implements SimpleObjectProvider<CuttingBoardBlock> {
	@Override
	public CuttingBoardBlock getObject() {
		return (CuttingBoardBlock) ModBlocks.CUTTING_BOARD.get();
	}

	@Override
	public Class<?> getType() {
		return CuttingBoardBlock.class;
	}
}
