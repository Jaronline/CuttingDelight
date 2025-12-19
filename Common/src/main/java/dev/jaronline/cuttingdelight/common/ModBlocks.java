package dev.jaronline.cuttingdelight.common;

import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import dev.jaronline.cuttingdelight.common.provider.ProviderManager;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;

public final class ModBlocks {
	public static final CustomCuttingBoardBlock CUTTING_BOARD = register("cutting_board", new CustomCuttingBoardBlock(
			BlockBehaviour.Properties.copy(ProviderManager.getObjectProvider(CuttingBoardBlock.class).getObject())
	));

	@SuppressWarnings("UnusedReturnValue")
	static Block bootstrap() {
		return CUTTING_BOARD;
	}

	static <B extends Block> B register(String id, B block) {
		return Registry.register(BuiltInRegistries.BLOCK, ModIds.cuttingDelightResource(id), block);
	}
}
