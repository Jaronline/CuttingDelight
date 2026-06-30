package dev.jaronline.cuttingdelight.forge.platform;

import dev.jaronline.cuttingdelight.common.platform.*;
import dev.jaronline.cuttingdelight.core.util.function.LazySupplier;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

public final class ForgePlatformHelper implements PlatformHelper {
	private final Supplier<ClientHelper> clientHelper = new LazySupplier<>(ClientHelper::new);
	private final Supplier<InventoryHelper> inventoryHelper = new LazySupplier<>(InventoryHelper::new);
	private final Supplier<ObjectHelper> objectHelper = new LazySupplier<>(ObjectHelper::new);
	private final Supplier<RecipeHelper> recipeHelper = new LazySupplier<>(RecipeHelper::new);

	@Override
	public PlatformClientHelper getClientHelper() {
		if (!FMLEnvironment.dist.isClient())
			throw new IllegalStateException("ClientHelper can only be accessed from client environments!");
		return clientHelper.get();
	}

	@Override
	public PlatformInventoryHelper getInventoryHelper() {
		return inventoryHelper.get();
	}

	@Override
	public PlatformObjectHelper getObjectHelper() {
		return objectHelper.get();
	}

	@Override
	public PlatformRecipeHelper<?> getRecipeHelper() {
		return recipeHelper.get();
	}
}
