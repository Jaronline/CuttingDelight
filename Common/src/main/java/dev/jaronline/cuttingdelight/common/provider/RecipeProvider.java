package dev.jaronline.cuttingdelight.common.provider;

import dev.jaronline.cuttingdelight.core.provider.SingletonProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public interface RecipeProvider<C extends Container> extends SingletonProvider {
	<T extends Recipe<C>> List<T> getRecipesFor(RecipeType<T> recipeType, Container container, Level level);
	ItemStack assemble(Recipe<C> recipe, Container container, RegistryAccess registryAccess);
	ItemStack getResultItem(Recipe<C> recipe, RegistryAccess registryAccess);
}
