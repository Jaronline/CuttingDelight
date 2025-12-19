package dev.jaronline.cuttingdelight.core.provider;

import com.google.common.reflect.TypeToken;

public interface SimpleProvider extends Provider {
	Class<?> getType();

	@Override
	default TypeToken<?> getToken() {
		return TypeToken.of(getType());
	}
}
