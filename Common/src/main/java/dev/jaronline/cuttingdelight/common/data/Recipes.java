package dev.jaronline.cuttingdelight.common.data;

import dev.jaronline.cuttingdelight.common.ModBlocks;
import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.platform.Services;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.item.FuelBlockItem;

import java.util.function.Consumer;

public class Recipes extends RecipeProvider {
	public Recipes(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> recipeConsumer) {
		FuelBlockItem cuttingBoardItem = Services.PLATFORM.getObjectHelper().getObject(
				FuelBlockItem.class, FarmersDelight.MODID + ":cutting_board");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.CUTTING_STATION)
				.requires(cuttingBoardItem)
				.unlockedBy("has_original_cutting_board", has(cuttingBoardItem))
				.save(recipeConsumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, Services.PLATFORM.getObjectHelper().getObject(CuttingBoardBlock.class))
				.requires(ModBlocks.CUTTING_STATION)
				.unlockedBy("has_original_cutting_board", has(cuttingBoardItem))
				.save(recipeConsumer, ModIds.CUTTING_DELIGHT_ID + ":farmers_cutting_board");
	}
}
