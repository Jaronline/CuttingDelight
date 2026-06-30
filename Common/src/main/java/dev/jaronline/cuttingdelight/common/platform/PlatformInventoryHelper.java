package dev.jaronline.cuttingdelight.common.platform;

import dev.jaronline.cuttingdelight.common.block.entity.CuttingStationBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface PlatformInventoryHelper {
    void setStackInSlot(CuttingStationBlockEntity inventorySource, int slot, ItemStack stack);
    Container asContainer(CuttingStationBlockEntity inventorySource);
}
