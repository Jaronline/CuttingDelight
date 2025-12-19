package dev.jaronline.cuttingdelight.core.provider;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

public interface Provider {
	TypeToken<?> getToken();

	default @Nullable String getId() {
		return null;
	}
}
