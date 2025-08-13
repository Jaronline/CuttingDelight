package dev.jaronline.cuttingdelight.common.registry;

import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public class BlockRegistry {
    private static BlockRegistry instance;

    private final DeferredRegister.Blocks blocks = DeferredRegister.createBlocks(CuttingDelight.MOD_ID);

    public final DeferredBlock<CustomCuttingBoardBlock> cuttingBoard = blocks.register("cutting_board",
            () -> new CustomCuttingBoardBlock(BlockBehaviour.Properties.ofFullCopy(ModBlocks.CUTTING_BOARD.get())));

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
