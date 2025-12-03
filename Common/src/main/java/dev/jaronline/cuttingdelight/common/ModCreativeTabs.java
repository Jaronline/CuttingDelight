package dev.jaronline.cuttingdelight.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

public final class ModCreativeTabs {
	public static final CreativeModeTab CUTTING_DELIGHT_TAB = register("cuttingdelight_tab",
			new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
					.title(Component.translatable("itemGroup.cuttingdelight"))
					.icon(() -> ModItems.CUTTING_BOARD.getDefaultInstance())
					.displayItems((params, output) -> {
						output.accept(ModItems.CUTTING_BOARD);
					})
					.build());

	@SuppressWarnings("UnusedReturnValue")
	static CreativeModeTab bootstrap() {
		return CUTTING_DELIGHT_TAB;
	}

	public static CreativeModeTab register(String identifier, CreativeModeTab tab) {
		return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ModIds.cuttingDelightResource(identifier), tab);
	}
}
