package dev.jaronline.cuttingdelight.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public final class ModCreativeTabs {
	private static final ResourceKey<CreativeModeTab> FARMERS_DELIGHT_TAB_KEY =
			ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(ModIds.FARMERS_DELIGHT_ID, "farmersdelight"));

	public static void registerItems(ResourceKey<CreativeModeTab> tabKey, Consumer<ItemLike> itemConsumer) {
		if (tabKey == FARMERS_DELIGHT_TAB_KEY) {
			itemConsumer.accept(ModItems.CUTTING_STATION);
		}
	}
}
