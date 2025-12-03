package dev.jaronline.cuttingdelight.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import vectorwing.farmersdelight.common.item.FuelBlockItem;

public final class ModItems {
	public static final FuelBlockItem CUTTING_BOARD = register("cutting_board",
			new FuelBlockItem(ModBlocks.CUTTING_BOARD, new Item.Properties(), 200));

	@SuppressWarnings("UnusedReturnValue")
	static Item boostrap() {
		return CUTTING_BOARD;
	}

	public static <I extends Item> I register(String identifier, I item) {
		return Registry.register(BuiltInRegistries.ITEM, ModIds.cuttingDelightResource(identifier), item);
	}
}
