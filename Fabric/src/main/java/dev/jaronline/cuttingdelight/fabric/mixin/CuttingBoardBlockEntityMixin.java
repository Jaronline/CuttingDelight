package dev.jaronline.cuttingdelight.fabric.mixin;

import dev.jaronline.cuttingdelight.common.ModBlockEntityTypes;
import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

@Mixin(CuttingBoardBlockEntity.class)
public class CuttingBoardBlockEntityMixin {
	@ModifyArg(
			method = "<init>(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
			at = @At(
					value = "INVOKE",
					target = "Lvectorwing/farmersdelight/common/block/entity/SyncedBlockEntity;<init>(Lnet/minecraft/world/level/block/entity/BlockEntityType;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
			),
			index = 0
	)
	private static BlockEntityType<?> cuttingdelight$fixCustomBlockEntityType(BlockEntityType<?> originalType, BlockPos pos, BlockState state) {
		if (state.getBlock() instanceof CustomCuttingBoardBlock) {
			return ModBlockEntityTypes.CUTTING_BOARD;
		}
		return originalType;
	}
}
