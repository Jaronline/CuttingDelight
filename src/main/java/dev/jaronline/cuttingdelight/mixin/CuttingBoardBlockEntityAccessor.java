package dev.jaronline.cuttingdelight.mixin;

import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

@Mixin(CuttingBoardBlockEntity.class)
public interface CuttingBoardBlockEntityAccessor {
    @Accessor("inventory")
    ItemStackHandler getInventory();

    @Accessor("isItemCarvingBoard")
    void setItemCarvingBoard(boolean value);
}
