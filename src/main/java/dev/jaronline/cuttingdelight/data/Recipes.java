package dev.jaronline.cuttingdelight.data;

import dev.jaronline.cuttingdelight.registry.BlockRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Recipes extends RecipeProvider {
    private final BlockRegistry blocks = BlockRegistry.getInstance();

    public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, blocks.cuttingBoard.get())
                .requires(ModItems.CUTTING_BOARD.get())
                .unlockedBy("has_original_cutting_board", has(ModItems.CUTTING_BOARD.get()))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, ModBlocks.CUTTING_BOARD.get())
                .requires(blocks.cuttingBoard.get())
                .unlockedBy("has_original_cutting_board", has(ModItems.CUTTING_BOARD.get()))
                .save(recipeOutput, "cuttingdelight:farmers_cutting_board");
    }
}
