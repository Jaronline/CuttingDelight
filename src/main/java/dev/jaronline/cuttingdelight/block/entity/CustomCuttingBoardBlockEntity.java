package dev.jaronline.cuttingdelight.block.entity;

import dev.jaronline.cuttingdelight.registry.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

public class CustomCuttingBoardBlockEntity extends CuttingBoardBlockEntity {
    public CustomCuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return BlockEntityTypeRegistry.getInstance().cuttingBoard.get();
    }
}
