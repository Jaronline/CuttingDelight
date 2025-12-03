package dev.jaronline.cuttingdelight.common.data;

import dev.jaronline.cuttingdelight.common.ModBlocks;
import dev.jaronline.cuttingdelight.common.ModIds;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.concurrent.CompletableFuture;

public class Recipes extends RecipeProvider {
	public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.CUTTING_BOARD)
				.requires(ModItems.CUTTING_BOARD.get())
				.unlockedBy("has_original_cutting_board", has(ModItems.CUTTING_BOARD.get()))
				.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, vectorwing.farmersdelight.common.registry.ModBlocks.CUTTING_BOARD.get())
				.requires(ModBlocks.CUTTING_BOARD)
				.unlockedBy("has_original_cutting_board", has(ModItems.CUTTING_BOARD.get()))
				.save(recipeOutput, ModIds.CUTTING_DELIGHT_ID + ":farmers_cutting_board");
	}
}
