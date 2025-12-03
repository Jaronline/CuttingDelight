package dev.jaronline.cuttingdelight.common.provider;

import net.minecraft.world.item.ItemStack;

public interface IInventoryProvider {
	void setStackInSlot(Object inventory, int slot, ItemStack stack);
}
