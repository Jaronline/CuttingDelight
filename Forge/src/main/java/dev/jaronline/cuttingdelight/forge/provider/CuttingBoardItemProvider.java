package dev.jaronline.cuttingdelight.forge.provider;

import dev.jaronline.cuttingdelight.common.provider.SimpleObjectProvider;
import dev.jaronline.cuttingdelight.core.provider.AutoProvider;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.item.FuelBlockItem;
import vectorwing.farmersdelight.common.registry.ModItems;

@AutoProvider
public class CuttingBoardItemProvider implements SimpleObjectProvider<FuelBlockItem> {
	@Override
	public FuelBlockItem getObject() {
		return (FuelBlockItem) ModItems.CUTTING_BOARD.get();
	}

	@Override
	public Class<?> getType() {
		return FuelBlockItem.class;
	}

	@Override
	public @Nullable String getId() {
		return FarmersDelight.MODID + ":cutting_board";
	}
}
