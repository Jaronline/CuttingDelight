package dev.jaronline.cuttingdelight.common.platform;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.List;

public interface PlatformRecipeHelper {
    List<ItemStack> rollResults(CuttingBoardRecipe recipe, RandomSource random, int fortuneLevel, CuttingBoardBlockEntity cuttingBoard);
}
