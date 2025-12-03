package dev.jaronline.cuttingdelight.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

@Mixin(CuttingBoardBlockEntity.class)
public interface CuttingBoardBlockEntityAccessor {
    @Accessor("isItemCarvingBoard")
    void setItemCarvingBoard(boolean value);
}
