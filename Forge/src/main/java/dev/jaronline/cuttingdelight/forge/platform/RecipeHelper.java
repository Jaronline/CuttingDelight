package dev.jaronline.cuttingdelight.forge.platform;

import dev.jaronline.cuttingdelight.common.platform.PlatformRecipeHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;

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

    private RecipeWrapper createWrapper(Container container) {
        ItemStackHandler itemStackHandler = new ItemStackHandler(container.getContainerSize());
        for (int i = 0; i < container.getContainerSize(); i++) {
            itemStackHandler.setStackInSlot(i, container.getItem(i));
        }
        return new RecipeWrapper(itemStackHandler);
    }
}
