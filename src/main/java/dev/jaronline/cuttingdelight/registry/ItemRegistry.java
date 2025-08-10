package dev.jaronline.cuttingdelight.registry;

import dev.jaronline.cuttingdelight.CuttingDelight;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    private static ItemRegistry instance;

    private final DeferredRegister.Items items = DeferredRegister.createItems(CuttingDelight.MOD_ID);

    private ItemRegistry() {}

    public static ItemRegistry getInstance() {
        if (instance == null)
            instance = new ItemRegistry();
        return instance;
    }

    public static void register(IEventBus bus) {
        getInstance().items.register(bus);
    }

    public DeferredRegister.Items getItemRegistry() {
        return items;
    }
}
