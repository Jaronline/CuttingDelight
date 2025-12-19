package dev.jaronline.cuttingdelight.common.provider;

import dev.jaronline.cuttingdelight.core.provider.SimpleProvider;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface InventoryProvider extends SimpleProvider {
	@Override
	default Class<Object> getType() {
		return Object.class;
	}

	void setStackInSlot(Object inventory, int slot, ItemStack stack);

	Container asContainer(Object inventory);
}
