package dev.jaronline.cuttingdelight.common.platform;

import dev.jaronline.cuttingdelight.common.block.entity.CuttingStationBlockEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.List;

public interface PlatformRecipeHelper<C extends Container> {
	<T extends Recipe<C>> List<T> getRecipesFor(RecipeType<T> recipeType, Container container, Level level);
	ItemStack assemble(Recipe<C> recipe, Container container, RegistryAccess registryAccess);
	ItemStack getResultItem(Recipe<C> recipe, RegistryAccess registryAccess);
	List<ItemStack> rollResults(CuttingBoardRecipe recipe, RandomSource random, int fortuneLevel, CuttingStationBlockEntity cuttingStation);
}
