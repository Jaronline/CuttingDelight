package dev.jaronline.cuttingdelight.common.platform;

import dev.jaronline.cuttingdelight.common.block.entity.CuttingStationBlockEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface PlatformRecipeHelper<C extends Container> {
	<T extends Recipe<C>> List<T> getRecipesFor(RecipeType<T> recipeType, Container container, Level level);
	ItemStack assemble(Recipe<C> recipe, Container container, RegistryAccess registryAccess);
	ItemStack getResultItem(Recipe<C> recipe, RegistryAccess registryAccess);
	List<ItemStack> rollResults(CuttingBoardRecipe recipe, RandomSource random, int fortuneLevel, CuttingStationBlockEntity cuttingStation);
	Optional<CuttingBoardRecipe> getMatchingRecipe(CuttingBoardBlockEntity cuttingBoard, ItemStack toolStack, @Nullable Player player);
}
