package dev.jaronline.cuttingdelight.common.provider;

import dev.jaronline.cuttingdelight.core.provider.Provider;

public interface ObjectProvider<O> extends Provider {
	O getObject();
}
