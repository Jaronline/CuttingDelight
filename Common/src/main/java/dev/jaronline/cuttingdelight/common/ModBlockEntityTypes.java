package dev.jaronline.cuttingdelight.common;

import dev.jaronline.cuttingdelight.common.block.entity.CuttingStationBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntityTypes {
	public static final BlockEntityType<CuttingStationBlockEntity> CUTTING_STATION = register("cutting_station",
			BlockEntityType.Builder.of(CuttingStationBlockEntity::new, ModBlocks.CUTTING_STATION).build(null));

	@SuppressWarnings("UnusedReturnValue")
	static BlockEntityType<?> bootstrap() {
		return CUTTING_STATION;
	}

	static <E extends BlockEntity> BlockEntityType<E> register(String id, BlockEntityType<E> blockEntityType) {
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModIds.cuttingDelightResource(id), blockEntityType);
	}
}
