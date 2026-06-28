package dev.jaronline.cuttingdelight.neoforge.platform;

import dev.jaronline.cuttingdelight.common.platform.PlatformRecipeHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.List;

public class RecipeHelper implements PlatformRecipeHelper {
    @Override
    public List<ItemStack> rollResults(CuttingBoardRecipe recipe, RandomSource random, int fortuneLevel, CuttingBoardBlockEntity cuttingBoard) {
        return recipe.rollResults(random, fortuneLevel, new RecipeWrapper(cuttingBoard.getInventory()));
    }
}
