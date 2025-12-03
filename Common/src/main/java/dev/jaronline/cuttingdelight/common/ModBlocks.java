package dev.jaronline.cuttingdelight.common;

import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class ModBlocks {
	public static final CustomCuttingBoardBlock CUTTING_BOARD = register("cutting_board", new CustomCuttingBoardBlock(
			BlockBehaviour.Properties.ofFullCopy(vectorwing.farmersdelight.common.registry.ModBlocks.CUTTING_BOARD.get())
	));

	@SuppressWarnings("UnusedReturnValue")
	static Block bootstrap() {
		return CUTTING_BOARD;
	}

	public static <B extends Block> B register(String identifier, B block) {
		return Registry.register(BuiltInRegistries.BLOCK, ModIds.cuttingDelightResource(identifier), block);
	}
}
