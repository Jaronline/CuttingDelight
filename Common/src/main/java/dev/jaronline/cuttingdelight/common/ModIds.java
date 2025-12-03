package dev.jaronline.cuttingdelight.common;

import net.minecraft.resources.ResourceLocation;

public final class ModIds {
	public static final String CUTTING_DELIGHT_ID = "cuttingdelight";

	public static ResourceLocation cuttingDelightResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(CUTTING_DELIGHT_ID, path);
	}
}
