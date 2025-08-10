package dev.jaronline.cuttingdelight.registry;

import dev.jaronline.cuttingdelight.CuttingDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntityTypeRegistry {
    private static BlockEntityTypeRegistry instance;

    private final DeferredRegister<BlockEntityType<?>> blockEntityTypes = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE, CuttingDelight.MOD_ID);

    private BlockEntityTypeRegistry() {}

    public static BlockEntityTypeRegistry getInstance() {
        if (instance == null)
            instance = new BlockEntityTypeRegistry();
        return instance;
    }

    public static void register(IEventBus bus) {
        getInstance().blockEntityTypes.register(bus);
    }

    public DeferredRegister<BlockEntityType<?>> getBlockEntityTypeRegistry() {
        return blockEntityTypes;
    }
}
