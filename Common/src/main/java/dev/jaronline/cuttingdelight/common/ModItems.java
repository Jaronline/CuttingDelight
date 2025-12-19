package dev.jaronline.cuttingdelight.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import vectorwing.farmersdelight.common.item.FuelBlockItem;

public final class ModItems {
	public static final FuelBlockItem CUTTING_STATION = register("cutting_station",
			new FuelBlockItem(ModBlocks.CUTTING_STATION, new Item.Properties(), 200));

	@SuppressWarnings("UnusedReturnValue")
	static Item bootstrap() {
		return CUTTING_STATION;
	}

	static <I extends Item> I register(String id, I item) {
		return Registry.register(BuiltInRegistries.ITEM, ModIds.cuttingDelightResource(id), item);
	}
}
