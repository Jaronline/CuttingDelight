package dev.jaronline.cuttingdelight.common;

import dev.jaronline.cuttingdelight.common.client.gui.menu.CuttingBoardMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class ModMenuTypes {
	public static final MenuType<CuttingBoardMenu> CUTTING_BOARD_MENU = register("cutting_board_menu",
			new MenuType<>(CuttingBoardMenu::new, FeatureFlags.DEFAULT_FLAGS));

	@SuppressWarnings("UnusedReturnValue")
	static MenuType<?> boostrap() {
		return CUTTING_BOARD_MENU;
	}

	public static <M extends AbstractContainerMenu> MenuType<M> register(String identifier, MenuType<M> menuType) {
		return Registry.register(BuiltInRegistries.MENU, ModIds.cuttingDelightResource(identifier), menuType);
	}
}
