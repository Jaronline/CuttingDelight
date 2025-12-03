package dev.jaronline.cuttingdelight.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public final class Bootstrapper {
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void boostrapByRegistryKey(ResourceKey<? extends Registry<?>> registryKey) {
		if (registryKey.equals(Registries.BLOCK)) {
			ModBlocks.bootstrap();
		} else if (registryKey.equals(Registries.BLOCK_ENTITY_TYPE)) {
			ModBlockEntityTypes.bootstrap();
		} else if (registryKey.equals(Registries.ITEM)) {
			ModItems.boostrap();
		} else if (registryKey.equals(Registries.CREATIVE_MODE_TAB)) {
			ModCreativeTabs.bootstrap();
		} else if (registryKey.equals(Registries.MENU)) {
			ModMenuTypes.boostrap();
		}
	}
}
