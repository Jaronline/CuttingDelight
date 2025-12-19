package dev.jaronline.cuttingdelight.forge.provider;

import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.common.provider.InventoryProvider;
import dev.jaronline.cuttingdelight.core.provider.AutoProvider;
import dev.jaronline.cuttingdelight.forge.world.ItemHandlerContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

@AutoProvider
public class CDInventoryProvider implements InventoryProvider {
	@Override
	public void setStackInSlot(Object inventory, int slot, ItemStack stack) {
		CustomCuttingBoardBlockEntity blockEntity = (CustomCuttingBoardBlockEntity) inventory;
		((IItemHandlerModifiable) blockEntity.getInventory()).setStackInSlot(slot, stack);
	}

	@Override
	public Container asContainer(Object inventory) {
		CustomCuttingBoardBlockEntity blockEntity = (CustomCuttingBoardBlockEntity) inventory;
		return new ItemHandlerContainer(blockEntity.getInventory());
	}
}
