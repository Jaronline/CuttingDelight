package dev.jaronline.cuttingdelight.common.provider;

import com.google.common.reflect.TypeToken;
import dev.jaronline.cuttingdelight.core.provider.ProviderMap;
import dev.jaronline.cuttingdelight.core.provider.ProviderNotFoundException;

public class ProviderManager {
	private static final ProviderMap<ObjectProvider<?>> objectProviders = new ProviderMap<>();
	private static InventoryProvider inventoryProvider;
	private static RecipeProvider recipeProvider;
	private static ClientPacketListenerProvider clientPacketListenerProvider;

	public static <O> ObjectProvider<O> getObjectProvider(Class<O> objectClass) {
		return getObjectProvider(TypeToken.of(objectClass));
	}

	public static <O> ObjectProvider<O> getObjectProvider(Class<O> objectClass, String id) {
		return getObjectProvider(TypeToken.of(objectClass), id);
	}

	public static <O> ObjectProvider<O> getObjectProvider(TypeToken<O> objectClass) {
		try {
			//noinspection unchecked
			return (ObjectProvider<O>) objectProviders.getOrThrow(objectClass);
		} catch (ProviderNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static <O> ObjectProvider<O> getObjectProvider(TypeToken<O> objectClass, String id) {
		try {
			//noinspection unchecked
			return (ObjectProvider<O>) objectProviders.getOrThrow(objectClass, id);
		} catch (ProviderNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void addObjectProvider(ObjectProvider<?> provider) {
		objectProviders.put(provider);
	}

	public static InventoryProvider getInventoryProvider() {
		if (inventoryProvider == null) {
			throw new IllegalStateException("Cannot access " + InventoryProvider.class.getSimpleName() +
					": it has not been set. Initialize it with " + ProviderManager.class.getSimpleName() + ".setInventoryProvider() first.");
		}
		return inventoryProvider;
	}

	public static void setInventoryProvider(InventoryProvider provider) {
		inventoryProvider = provider;
	}

	public static RecipeProvider getRecipeProvider() {
		if (recipeProvider == null) {
			throw new IllegalStateException("Cannot access " + RecipeProvider.class.getSimpleName() +
					": it has not been set. Initialize it with " + ProviderManager.class.getSimpleName() + ".setRecipeProvider() first.");
		}
		return recipeProvider;
	}

	public static void setRecipeProvider(RecipeProvider provider) {
		recipeProvider = provider;
	}

	public static ClientPacketListenerProvider getClientPacketListenerProvider() {
		if (clientPacketListenerProvider == null) {
			throw new IllegalStateException("Cannot access " + ClientPacketListenerProvider.class.getSimpleName() +
					": it has not been set. Initialize it with " + ProviderManager.class.getSimpleName() + ".setClientPacketListenerProvider() first.");
		}
		return clientPacketListenerProvider;
	}

	public static void setClientPacketListenerProvider(ClientPacketListenerProvider provider) {
		clientPacketListenerProvider = provider;
	}

	private ProviderManager() {}
}
