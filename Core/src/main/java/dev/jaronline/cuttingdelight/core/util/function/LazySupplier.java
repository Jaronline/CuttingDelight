package dev.jaronline.cuttingdelight.core.util.function;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class LazySupplier<T> implements Supplier<T> {
	@Nonnull
	private final Supplier<T> supplier;
	private T cachedResult;
	private boolean initialized;

	public LazySupplier(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	@Override
	public T get() {
		if (!initialized) {
			cachedResult = supplier.get();
			initialized = true;
		}
		return cachedResult;
	}
}
