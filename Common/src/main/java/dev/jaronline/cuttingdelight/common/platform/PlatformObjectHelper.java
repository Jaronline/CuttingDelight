package dev.jaronline.cuttingdelight.common.platform;

import com.google.common.reflect.TypeToken;
import dev.jaronline.cuttingdelight.core.util.ObjectMap;
import dev.jaronline.cuttingdelight.core.util.ObjectNotFoundException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class PlatformObjectHelper {
	private final ObjectMap objects = new ObjectMap();

	public PlatformObjectHelper() {
		setObjects();
	}

	protected abstract void setObjects();

	protected final <T extends Block> ObjectBuilder<T> block(Class<T> blockClass) {
		return new ObjectBuilder<>(objects, TypeToken.of(blockClass));
	}

	protected final <T extends BlockItem> ObjectBuilder<T> blockItem(Class<T> blockItemClass) {
		return new ObjectBuilder<>(objects, TypeToken.of(blockItemClass));
	}

	protected final <T extends Recipe<?>> ObjectBuilder<RecipeType<T>> recipeType(TypeToken<RecipeType<T>> type) {
		return new ObjectBuilder<>(objects, type);
	}

	protected final ObjectBuilder<SoundEvent> soundEvent() {
		return new ObjectBuilder<>(objects, TypeToken.of(SoundEvent.class));
	}

	public final SoundEvent getSoundEvent(String id) {
		return getObject(SoundEvent.class, id);
	}

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

	protected static class ObjectBuilder<T> {
		private final ObjectMap objects;
		private final TypeToken<T> type;
		private @Nullable String id;

		private ObjectBuilder(ObjectMap objects, TypeToken<T> type) {
			this.objects = objects;
			this.type = type;
		}

		public ObjectBuilder<T> id(ResourceLocation resource) {
			return id(resource.toString());
		}

		public ObjectBuilder<T> id(String id) {
			this.id = id;
			return this;
		}

		public void save(Supplier<T> value) {
			if (id != null) {
				objects.put(type, id, value);
				return;
			}
			objects.put(type, value);
		}
	}
}
