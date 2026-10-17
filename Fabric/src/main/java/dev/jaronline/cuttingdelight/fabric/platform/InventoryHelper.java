package dev.jaronline.cuttingdelight.fabric.platform;

import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.common.platform.PlatformInventoryHelper;
import net.minecraft.world.item.ItemStack;

public class InventoryHelper implements PlatformInventoryHelper {
	@Override
	public void setStackInSlot(Object inventory, int slot, ItemStack stack) {
		CustomCuttingBoardBlockEntity blockEntity = (CustomCuttingBoardBlockEntity) inventory;
		blockEntity.getInventory().setStackInSlot(slot, stack);
	}
}
