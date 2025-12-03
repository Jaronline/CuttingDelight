package dev.jaronline.cuttingdelight.neoforge.provider;

import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.common.provider.IInventoryProvider;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public class InventoryProvider implements IInventoryProvider {
	@Override
	public void setStackInSlot(Object inventory, int slot, ItemStack stack) {
		CustomCuttingBoardBlockEntity blockEntity = (CustomCuttingBoardBlockEntity) inventory;
		((IItemHandlerModifiable) blockEntity.getInventory()).setStackInSlot(slot, stack);
	}
}
