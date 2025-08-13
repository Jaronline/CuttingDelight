package dev.jaronline.cuttingdelight.integration.jei;

import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.registry.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.integration.jei.FDRecipeTypes;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CuttingDelight.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ItemRegistry.getInstance().cuttingBoard.get()),
                FDRecipeTypes.CUTTING);
    }
}
