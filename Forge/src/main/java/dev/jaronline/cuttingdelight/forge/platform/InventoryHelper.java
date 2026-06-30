package dev.jaronline.cuttingdelight.forge.platform;

import dev.jaronline.cuttingdelight.common.block.entity.CuttingStationBlockEntity;
import dev.jaronline.cuttingdelight.common.platform.PlatformInventoryHelper;
import dev.jaronline.cuttingdelight.forge.world.ItemHandlerContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public final class InventoryHelper implements PlatformInventoryHelper {
	@Override
	public void setStackInSlot(CuttingStationBlockEntity inventorySource, int slot, ItemStack stack) {
		((IItemHandlerModifiable) inventorySource.getInventory()).setStackInSlot(slot, stack);
	}

	@Override
	public Container asContainer(CuttingStationBlockEntity inventorySource) {
		return new ItemHandlerContainer(inventorySource.getInventory());
	}
}
