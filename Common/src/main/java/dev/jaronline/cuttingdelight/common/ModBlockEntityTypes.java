package dev.jaronline.cuttingdelight.common;

import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntityTypes {
	public static final BlockEntityType<? extends CustomCuttingBoardBlockEntity> CUTTING_BOARD = register("cutting_board",
			BlockEntityType.Builder.of(CustomCuttingBoardBlockEntity::new, ModBlocks.CUTTING_BOARD).build(null));

	@SuppressWarnings("UnusedReturnValue")
	static BlockEntityType<?> bootstrap() {
		return CUTTING_BOARD;
	}

	public static <E extends BlockEntity> BlockEntityType<E> register(String identifier, BlockEntityType<E> blockEntityType) {
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ModIds.cuttingDelightResource(identifier), blockEntityType);
	}
}
