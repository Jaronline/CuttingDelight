package dev.jaronline.cuttingdelight.core.util;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public final class ObjectMap {
    private final Map<KeyNode, Supplier<?>> internalMap = new HashMap<>();

    public @Nullable <T> Supplier<T> put(Class<T> key, Supplier<T> value) {
        return put(TypeToken.of(key), value);
    }

    public @Nullable <T> Supplier<T> put(Class<T> key, String id, Supplier<T> value) {
        return put(TypeToken.of(key), id, value);
    }

    public @Nullable <T> Supplier<T> put(TypeToken<T> key, Supplier<T> value) {
        return (Supplier<T>) internalMap.put(new KeyNode(key), value);
    }

    public @Nullable <T> Supplier<T> put(TypeToken<T> key, String id, Supplier<T> value) {
        return (Supplier<T>) internalMap.put(new KeyNode(key, id), value);
    }

    public <T> Supplier<T> get(Class<T> key) {
        return get(TypeToken.of(key));
    }

    public <T> Supplier<T> get(TypeToken<T> key) {
        return (Supplier<T>) internalMap.get(new KeyNode(key));
    }

    public <T> Supplier<T> get(Class<T> key, String id) {
        return get(TypeToken.of(key), id);
    }

    public <T> Supplier<T> get(TypeToken<T> key, String id) {
        return (Supplier<T>) internalMap.get(new KeyNode(key, id));
    }

    public <T> Supplier<T> getOrThrow(Class<T> key) throws ObjectNotFoundException {
        return getOrThrow(TypeToken.of(key));
    }

    public <T> Supplier<T> getOrThrow(TypeToken<T> key) throws ObjectNotFoundException {
        return getOrThrow(new KeyNode(key));
    }

    public <T> Supplier<T> getOrThrow(Class<T> key, String id) throws ObjectNotFoundException {
        return getOrThrow(TypeToken.of(key), id);
    }

    public <T> Supplier<T> getOrThrow(TypeToken<T> key, String id) throws ObjectNotFoundException {
        return getOrThrow(new KeyNode(key, id));
    }

    private <T> Supplier<T> getOrThrow(KeyNode keyNode) throws ObjectNotFoundException {
        if (!internalMap.containsKey(keyNode)) {
            if (keyNode.id == null) {
                throw new ObjectNotFoundException(keyNode.token);
            } else {
                throw new ObjectNotFoundException(keyNode.token, keyNode.id);
            }
        }
        return (Supplier<T>) internalMap.get(keyNode);
    }

    private record KeyNode(TypeToken<?> token, @Nullable String id) {
        private KeyNode(TypeToken<?> token) {
            this(token, null);
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
            if (!(obj instanceof KeyNode node)) return false;
            if (!token.equals(node.token)) return false;
            if (id == null && node.id == null) return true;
            if (id != null) return id.equals(node.id);
            return false;
        }
    }
}
