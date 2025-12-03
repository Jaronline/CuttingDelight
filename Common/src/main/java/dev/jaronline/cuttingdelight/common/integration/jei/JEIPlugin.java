package dev.jaronline.cuttingdelight.common.integration.jei;

import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.integration.jei.FDRecipeTypes;

@JeiPlugin
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return ModIds.cuttingDelightResource("jei_plugin");
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(ModItems.CUTTING_BOARD), FDRecipeTypes.CUTTING);
	}
}
