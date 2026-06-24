package dev.jaronline.cuttingdelight.common.platform;

import net.minecraft.world.item.ItemStack;

public interface PlatformInventoryHelper {
	void setStackInSlot(Object inventory, int slot, ItemStack stack);
}
