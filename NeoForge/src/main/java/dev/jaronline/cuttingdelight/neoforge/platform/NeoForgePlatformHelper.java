package dev.jaronline.cuttingdelight.neoforge.platform;

import dev.jaronline.cuttingdelight.common.platform.PlatformClientHelper;
import dev.jaronline.cuttingdelight.common.platform.PlatformHelper;
import dev.jaronline.cuttingdelight.common.platform.PlatformInventoryHelper;
import dev.jaronline.cuttingdelight.common.platform.PlatformRecipeHelper;
import dev.jaronline.cuttingdelight.core.util.function.LazySupplier;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

public class NeoForgePlatformHelper implements PlatformHelper {
	private final Supplier<InventoryHelper> inventoryHelper = new LazySupplier<>(InventoryHelper::new);
	private final Supplier<ClientHelper> clientHelper = new LazySupplier<>(ClientHelper::new);
	private final Supplier<RecipeHelper> recipeHelper = new LazySupplier<>(RecipeHelper::new);

	@Override
	public PlatformInventoryHelper getInventoryHelper() {
		return inventoryHelper.get();
	}

	@Override
	public PlatformClientHelper getClientHelper() {
		if (!FMLEnvironment.dist.isClient())
			throw new IllegalStateException("ClientHelper can only be accessed from client environments!");
		return clientHelper.get();
	}

	@Override
	public PlatformRecipeHelper getRecipeHelper() {
		return recipeHelper.get();
	}
}
