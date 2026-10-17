package dev.jaronline.cuttingdelight.fabric.platform;

import dev.jaronline.cuttingdelight.common.platform.PlatformClientHelper;
import dev.jaronline.cuttingdelight.common.platform.PlatformHelper;
import dev.jaronline.cuttingdelight.common.platform.PlatformInventoryHelper;
import dev.jaronline.cuttingdelight.common.platform.PlatformRecipeHelper;
import dev.jaronline.cuttingdelight.common.util.function.LazySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

public class FabricPlatformHelper implements PlatformHelper {
	private final Supplier<InventoryHelper> inventoryHelper = new LazySupplier<>(InventoryHelper::new);
	private final Supplier<ClientHelper> clientHelper = new LazySupplier<>(ClientHelper::new);
	private final Supplier<RecipeHelper> recipeHelper = new LazySupplier<>(RecipeHelper::new);

	@Override
	public PlatformInventoryHelper getInventoryHelper() {
		return inventoryHelper.get();
	}

	@Override
	public PlatformClientHelper getClientHelper() {
		if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT)
			throw new IllegalStateException("ClientHelper can only be accessed from client environments!");
		return clientHelper.get();
	}

	@Override
	public PlatformRecipeHelper getRecipeHelper() {
		return recipeHelper.get();
	}
}
