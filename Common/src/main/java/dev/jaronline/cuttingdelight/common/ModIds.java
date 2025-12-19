package dev.jaronline.cuttingdelight.common;

import net.minecraft.resources.ResourceLocation;

public final class ModIds {
	public static final String CUTTING_DELIGHT_ID = "cuttingdelight";
	public static final String FARMERS_DELIGHT_ID = "farmersdelight";

	public static ResourceLocation cuttingDelightResource(String path) {
		return new ResourceLocation(CUTTING_DELIGHT_ID, path);
	}
}
