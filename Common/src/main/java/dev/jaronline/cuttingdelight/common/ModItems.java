package dev.jaronline.cuttingdelight.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public final class ModItems {
	public static final BlockItem CUTTING_BOARD = register("cutting_board",
			new BlockItem(ModBlocks.CUTTING_BOARD, new Item.Properties()));

	@SuppressWarnings("UnusedReturnValue")
	static Item boostrap() {
		return CUTTING_BOARD;
	}

	public static <I extends Item> I register(String identifier, I item) {
		return Registry.register(BuiltInRegistries.ITEM, ModIds.cuttingDelightResource(identifier), item);
	}
}
