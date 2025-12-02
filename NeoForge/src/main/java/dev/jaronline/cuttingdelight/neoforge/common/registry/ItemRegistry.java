package dev.jaronline.cuttingdelight.neoforge.common.registry;

import dev.jaronline.cuttingdelight.neoforge.CuttingDelight;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.item.FuelBlockItem;

public class ItemRegistry {
    private static ItemRegistry instance;

    private final BlockRegistry blocks = BlockRegistry.getInstance();
    private final DeferredRegister.Items items = DeferredRegister.createItems(CuttingDelight.MOD_ID);

    public final DeferredItem<FuelBlockItem> cuttingBoard = items.register("cutting_board",
            () -> new FuelBlockItem(blocks.cuttingBoard.get(), new Item.Properties(), 200));

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
