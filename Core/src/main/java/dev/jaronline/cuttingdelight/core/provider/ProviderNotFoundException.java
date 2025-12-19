package dev.jaronline.cuttingdelight.core.provider;

import com.google.common.reflect.TypeToken;

public class ProviderNotFoundException extends Exception {
	public ProviderNotFoundException() {
		super();
	}

	public ProviderNotFoundException(String message) {
		super(message);
	}

	public ProviderNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProviderNotFoundException(TypeToken<?> typeToken) {
		super("No provider found for key: " + typeToken);
	}

	public ProviderNotFoundException(TypeToken<?> typeToken, String id) {
		super("No provider found for key: " + typeToken + " with id: " + id);
	}
}
