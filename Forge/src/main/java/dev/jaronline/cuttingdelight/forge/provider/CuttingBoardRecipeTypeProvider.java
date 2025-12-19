package dev.jaronline.cuttingdelight.forge.provider;

import com.google.common.reflect.TypeToken;
import dev.jaronline.cuttingdelight.common.provider.ObjectProvider;
import dev.jaronline.cuttingdelight.core.provider.AutoProvider;
import net.minecraft.world.item.crafting.RecipeType;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

@AutoProvider
public class CuttingBoardRecipeTypeProvider implements ObjectProvider<RecipeType<CuttingBoardRecipe>> {
	@Override
	public RecipeType<CuttingBoardRecipe> getObject() {
		return ModRecipeTypes.CUTTING.get();
	}

	@Override
	public TypeToken<?> getToken() {
		return new TypeToken<RecipeType<CuttingBoardRecipe>>() {};
	}
}
