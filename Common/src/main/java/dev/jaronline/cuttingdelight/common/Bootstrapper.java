package dev.jaronline.cuttingdelight.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

@SuppressWarnings("ResultOfMethodCallIgnored")
public final class Bootstrapper {
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

	public static void boostrapAll() {
		ModBlocks.bootstrap();
		ModBlockEntityTypes.bootstrap();
		ModItems.boostrap();
		ModCreativeTabs.bootstrap();
		ModMenuTypes.boostrap();
	}
}
