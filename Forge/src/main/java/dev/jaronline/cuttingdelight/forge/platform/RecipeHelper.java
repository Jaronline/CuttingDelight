package dev.jaronline.cuttingdelight.forge.platform;

import dev.jaronline.cuttingdelight.common.block.entity.CuttingStationBlockEntity;
import dev.jaronline.cuttingdelight.common.platform.PlatformRecipeHelper;
import dev.jaronline.cuttingdelight.forge.mixin.CuttingBoardBlockEntityInvoker;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.List;
import java.util.Optional;

public final class RecipeHelper implements PlatformRecipeHelper<RecipeWrapper> {
	@Override
	public <T extends Recipe<RecipeWrapper>> List<T> getRecipesFor(RecipeType<T> recipeType, Container container, Level level) {
		return level.getRecipeManager().getRecipesFor(recipeType, createWrapper(container), level);
	}

	@Override
	public ItemStack assemble(Recipe<RecipeWrapper> recipe, Container container, RegistryAccess registryAccess) {
		return recipe.assemble(createWrapper(container), registryAccess);
	}

	@Override
	public ItemStack getResultItem(Recipe<RecipeWrapper> recipe, RegistryAccess registryAccess) {
		return recipe.getResultItem(registryAccess);
	}

	@Override
	public List<ItemStack> rollResults(CuttingBoardRecipe recipe, RandomSource random, int fortuneLevel, CuttingStationBlockEntity cuttingStation) {
		return recipe.rollResults(random, fortuneLevel, createWrapper(cuttingStation));
	}

	@Override
	public Optional<CuttingBoardRecipe> getMatchingRecipe(CuttingBoardBlockEntity cuttingBoard, ItemStack toolStack, @Nullable Player player) {
		return ((CuttingBoardBlockEntityInvoker) cuttingBoard).cuttingdelight$getMatchingRecipe(createWrapper(cuttingBoard), toolStack, player);
	}

	private RecipeWrapper createWrapper(CuttingBoardBlockEntity cuttingBoard) {
		return new RecipeWrapper((IItemHandlerModifiable) cuttingBoard.getInventory());
	}

	private RecipeWrapper createWrapper(Container container) {
		ItemStackHandler itemStackHandler = new ItemStackHandler(container.getContainerSize());
		for (int i = 0; i < container.getContainerSize(); i++) {
			itemStackHandler.setStackInSlot(i, container.getItem(i));
		}
		return new RecipeWrapper(itemStackHandler);
	}
}
