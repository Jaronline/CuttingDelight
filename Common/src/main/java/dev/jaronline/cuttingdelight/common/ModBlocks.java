package dev.jaronline.cuttingdelight.common;

import dev.jaronline.cuttingdelight.common.block.CuttingStationBlock;
import dev.jaronline.cuttingdelight.common.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;

public final class ModBlocks {
	public static final CuttingStationBlock CUTTING_STATION = register("cutting_station", new CuttingStationBlock(
			BlockBehaviour.Properties.copy(Services.PLATFORM.getObjectHelper().getObject(CuttingBoardBlock.class))
	));

	@SuppressWarnings("UnusedReturnValue")
	static Block bootstrap() {
		return CUTTING_STATION;
	}

	static <B extends Block> B register(String id, B block) {
		return Registry.register(BuiltInRegistries.BLOCK, ModIds.cuttingDelightResource(id), block);
	}
}
