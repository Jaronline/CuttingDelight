package dev.jaronline.cuttingdelight.common.util.function;

import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class LazySupplier<T> implements Supplier<T> {
	private final Supplier<T> supplier;
	@Nullable
	private T cachedResult;
	private boolean initialized;

	public LazySupplier(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	@Override
	@Nullable
	public T get() {
		if (!initialized) {
			cachedResult = supplier.get();
			initialized = true;
		}
		return cachedResult;
	}
}
