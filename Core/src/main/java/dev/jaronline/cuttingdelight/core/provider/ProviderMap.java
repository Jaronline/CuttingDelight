package dev.jaronline.cuttingdelight.core.provider;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProviderMap<P extends Provider> {
	private final Map<KeyNode, P> internalMap = new HashMap<>();

	public @Nullable P put(P provider) {
		return internalMap.put(new KeyNode(provider.getToken(), provider.getId()), provider);
	}

	public P get(TypeToken key) {
		return internalMap.get(new KeyNode(key));
	}

	public P get(TypeToken key, String id) {
		return internalMap.get(new KeyNode(key, id));
	}

	public P getOrThrow(TypeToken key) throws ProviderNotFoundException {
		return getOrThrow(new KeyNode(key));
	}

	public P getOrThrow(TypeToken key, String id) throws ProviderNotFoundException {
		return getOrThrow(new KeyNode(key, id));
	}

	private P getOrThrow(KeyNode keyNode) throws ProviderNotFoundException {
		if (!internalMap.containsKey(keyNode)) {
			if (keyNode.id == null) {
				throw new ProviderNotFoundException(keyNode.token);
			} else {
				throw new ProviderNotFoundException(keyNode.token, keyNode.id);
			}
		}
		return internalMap.get(keyNode);
	}

	private static class KeyNode {
		private final TypeToken<?> token;
		private final @Nullable String id;

		private KeyNode(TypeToken<?> token) {
			this(token, null);
		}

		private KeyNode(TypeToken<?> token, @Nullable String id) {
			this.token = token;
			this.id = id;
		}

		@Override
		public String toString() {
			return "KeyNode{" +
					"token=" + token +
					", id='" + id + '\'' +
					'}';
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ProviderMap.KeyNode node)) return false;
			if (!token.equals(node.token)) return false;
			if (id == null && node.id == null) return true;
			if (id != null) return id.equals(node.id);
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(token, id);
		}
	}
}
