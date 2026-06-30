package dev.jaronline.cuttingdelight.core.util;

import com.google.common.reflect.TypeToken;

public class ObjectNotFoundException extends Exception {
	ObjectNotFoundException(TypeToken<?> typeToken) {
		super("Could not find object of type " + typeToken);
	}

	ObjectNotFoundException(TypeToken<?> typeToken, String id) {
		super("Could not find object of type " + typeToken + " with id: " + id);
	}
}
