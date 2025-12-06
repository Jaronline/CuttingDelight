package dev.jaronline.cuttingdelight.common.provider;

public class ProviderManager {
	private static IClientCommonPacketListenerProvider clientCommonPacketListenerProvider;
	private static IInventoryProvider inventoryProvider;

	public static IClientCommonPacketListenerProvider getClientCommonPacketListenerProvider() {
		if (clientCommonPacketListenerProvider == null) {
			unsetError(IClientCommonPacketListenerProvider.class);
		}
		return clientCommonPacketListenerProvider;
	}

	public static void setClientCommonPacketListenerProvider(IClientCommonPacketListenerProvider provider) {
		clientCommonPacketListenerProvider = provider;
	}

	public static IInventoryProvider getInventoryProvider() {
		if (inventoryProvider == null) {
			unsetError(IInventoryProvider.class);
		}
		return inventoryProvider;
	}

	public static void setInventoryProvider(IInventoryProvider provider) {
		inventoryProvider = provider;
	}

	private static void unsetError(Class<?> providerClass) {
		throw new IllegalStateException(providerClass.getSimpleName() + " has not been set!");
	}

	private ProviderManager() {}
}
