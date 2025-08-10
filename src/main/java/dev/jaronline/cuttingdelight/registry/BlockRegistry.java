package dev.jaronline.cuttingdelight.registry;

import dev.jaronline.cuttingdelight.CuttingDelight;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    private static BlockRegistry instance;

    private final DeferredRegister.Blocks blocks = DeferredRegister.createBlocks(CuttingDelight.MOD_ID);

    private BlockRegistry() {}

    public static BlockRegistry getInstance() {
        if (instance == null)
            instance = new BlockRegistry();
        return instance;
    }

    public static void register(IEventBus bus) {
        getInstance().blocks.register(bus);
    }

    public DeferredRegister.Blocks getBlockRegistry() {
        return blocks;
    }
}
