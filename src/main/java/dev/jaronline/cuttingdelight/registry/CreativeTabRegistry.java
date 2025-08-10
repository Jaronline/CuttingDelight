package dev.jaronline.cuttingdelight.registry;

import dev.jaronline.cuttingdelight.CuttingDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTabRegistry {
    private static CreativeTabRegistry instance;

    private final ItemRegistry itemRegistry = ItemRegistry.getInstance();
    private final DeferredRegister<CreativeModeTab> creativeTabs = DeferredRegister.create(
            Registries.CREATIVE_MODE_TAB, CuttingDelight.MOD_ID);

    private final DeferredHolder<CreativeModeTab, CreativeModeTab> cuttingDelightTab = creativeTabs
            .register("cuttingdelight_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.cuttingdelight"))
                .build());

    private CreativeTabRegistry() {}

    public static CreativeTabRegistry getInstance() {
        if (instance == null)
            instance = new CreativeTabRegistry();
        return instance;
    }

    public static void register(IEventBus bus) {
        getInstance().creativeTabs.register(bus);
        bus.addListener(getInstance()::addCreative);
    }

    public DeferredRegister<CreativeModeTab> getTabRegistry() {
        return creativeTabs;
    }

    public DeferredHolder<CreativeModeTab, CreativeModeTab> getCuttingDelightTab() {
        return cuttingDelightTab;
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }
}
