package dev.jaronline.cuttingdelight.registry;

import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.gui.menu.CuttingBoardMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuTypeRegistry {
    private static MenuTypeRegistry instance;

    private final DeferredRegister<MenuType<?>> menuTypes = DeferredRegister.create(
            Registries.MENU, CuttingDelight.MOD_ID);

    public final DeferredHolder<MenuType<?>, MenuType<CuttingBoardMenu>> cuttingBoardMenu = menuTypes.register(
            "cutting_board_menu", () -> new MenuType<>(CuttingBoardMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private MenuTypeRegistry() {}

    public static MenuTypeRegistry getInstance() {
        if (instance == null) {
            instance = new MenuTypeRegistry();
        }
        return instance;
    }

    public static void register(IEventBus bus) {
        getInstance().menuTypes.register(bus);
    }

    public DeferredRegister<MenuType<?>> getMenuTypes() {
        return menuTypes;
    }
}
