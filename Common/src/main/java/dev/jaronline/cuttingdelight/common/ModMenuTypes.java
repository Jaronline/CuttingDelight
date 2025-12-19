package dev.jaronline.cuttingdelight.common;

import dev.jaronline.cuttingdelight.common.client.gui.menu.CuttingStationMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class ModMenuTypes {
	public static final MenuType<CuttingStationMenu> CUTTING_STATION_MENU = register("cutting_station_menu",
			new MenuType<>(CuttingStationMenu::new, FeatureFlags.DEFAULT_FLAGS));

	@SuppressWarnings("UnusedReturnValue")
	static MenuType<?> bootstrap() {
		return CUTTING_STATION_MENU;
	}

	static <M extends AbstractContainerMenu> MenuType<M> register(String id, MenuType<M> menuType) {
		return Registry.register(BuiltInRegistries.MENU, ModIds.cuttingDelightResource(id), menuType);
	}
}
