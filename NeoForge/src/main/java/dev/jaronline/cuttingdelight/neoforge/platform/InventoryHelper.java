package dev.jaronline.cuttingdelight.neoforge.platform;

import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.common.platform.PlatformInventoryHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class InventoryHelper implements PlatformInventoryHelper {
    @Override
    public void setStackInSlot(Object inventory, int slot, ItemStack stack) {
        CustomCuttingBoardBlockEntity blockEntity = (CustomCuttingBoardBlockEntity) inventory;
		((IItemHandlerModifiable) blockEntity.getInventory()).setStackInSlot(slot, stack);
    }
}
