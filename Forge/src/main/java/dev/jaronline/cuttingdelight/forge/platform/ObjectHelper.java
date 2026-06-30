package dev.jaronline.cuttingdelight.forge.platform;

import com.google.common.reflect.TypeToken;
import dev.jaronline.cuttingdelight.common.platform.PlatformObjectHelper;
import dev.jaronline.cuttingdelight.core.util.ObjectMap;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.item.FuelBlockItem;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

public final class ObjectHelper extends PlatformObjectHelper {
    @Override
    protected void setObjects(ObjectMap objects) {
        objects.put(CuttingBoardBlock.class, () -> (CuttingBoardBlock)ModBlocks.CUTTING_BOARD.get());
        objects.put(FuelBlockItem.class, FarmersDelight.MODID + ":cutting_board",
                () -> (FuelBlockItem)ModItems.CUTTING_BOARD.get());
        objects.put(new TypeToken<>() {}, ModRecipeTypes.CUTTING);
    }
}
