package dev.jaronline.cuttingdelight.common.platform;

import com.google.common.reflect.TypeToken;
import dev.jaronline.cuttingdelight.core.util.ObjectMap;
import dev.jaronline.cuttingdelight.core.util.ObjectNotFoundException;

public abstract class PlatformObjectHelper {
	private final ObjectMap objects = new ObjectMap();

	public PlatformObjectHelper() {
		setObjects(objects);
	}

	protected abstract void setObjects(ObjectMap objects);

	public final <T> T getObject(Class<T> objectClass) {
		return getObject(() -> objects.getOrThrow(objectClass).get());
	}

	public final <T> T getObject(Class<T> objectClass, String id) {
		return getObject(() -> objects.getOrThrow(objectClass, id).get());
	}

	public final <T> T getObject(TypeToken<T> type) {
		return getObject(() -> objects.getOrThrow(type).get());
	}

	public final <T> T getObject(TypeToken<T> type, String id) {
		return getObject(() -> objects.getOrThrow(type, id).get());
	}

	private <T> T getObject(ObjectSupplier<T> supplier) {
		try {
			return supplier.get();
		} catch (ObjectNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@FunctionalInterface
	private interface ObjectSupplier<T> {
		T get() throws ObjectNotFoundException;
	}
}
