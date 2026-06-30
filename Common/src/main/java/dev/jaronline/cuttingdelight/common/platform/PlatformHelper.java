package dev.jaronline.cuttingdelight.common.platform;

public interface PlatformHelper {
	PlatformClientHelper getClientHelper();
	PlatformInventoryHelper getInventoryHelper();
	PlatformObjectHelper getObjectHelper();
	@SuppressWarnings("rawtypes")
	PlatformRecipeHelper getRecipeHelper();
}
