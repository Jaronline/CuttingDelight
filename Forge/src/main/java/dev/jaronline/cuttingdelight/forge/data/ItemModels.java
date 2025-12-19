package dev.jaronline.cuttingdelight.forge.data;

import com.mojang.logging.LogUtils;
import dev.jaronline.cuttingdelight.common.ModIds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ItemModels extends ItemModelProvider {
	public static final String GENERATED = "item/generated";
	private static final Logger LOGGER = LogUtils.getLogger();

	public ItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, ModIds.CUTTING_DELIGHT_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		Set<Item> items = ForgeRegistries.ITEMS.getValues().stream().filter(i -> ModIds.CUTTING_DELIGHT_ID.equals(
				Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(i)).getNamespace())).collect(Collectors.toSet());

		takeAll(items, i -> i instanceof BlockItem).forEach(item -> blockBasedModel(item, ""));

		items.forEach(item -> itemGeneratedModel(item, resourceItem(itemName(item))));
	}

	public void blockBasedModel(Item item, String suffix) {
		withExistingParent(itemName(item), resourceBlock(itemName(item) + suffix));
	}

	public void itemGeneratedModel(Item item, ResourceLocation texture) {
		withExistingParent(itemName(item), GENERATED).texture("layer0", texture);
	}

	private String itemName(Item item) {
		return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).getPath();
	}

	public ResourceLocation resourceBlock(String path) {
		return ModIds.cuttingDelightResource("block/" + path);
	}

	public ResourceLocation resourceItem(String path) {
		return ModIds.cuttingDelightResource("item/" + path);
	}

	@SafeVarargs
	@SuppressWarnings("varargs")
	public static <T> Collection<T> takeAll(Set<? extends T> src, T... items) {
		List<T> ret = Arrays.asList(items);
		for (T item : items) {
			if (!src.contains(item)) {
				LOGGER.warn("Item {} not found in set", item);
			}
		}
		if (!src.removeAll(ret)) {
			LOGGER.warn("takeAll array didn't yield anything ({})", Arrays.toString(items));
		}
		return ret;
	}

	public static <T> Collection<T> takeAll(Set<T> src, Predicate<T> pred) {
		List<T> ret = new ArrayList<>();

		Iterator<T> iter = src.iterator();
		while (iter.hasNext()) {
			T item = iter.next();
			if (pred.test(item)) {
				iter.remove();
				ret.add(item);
			}
		}

		if (ret.isEmpty()) {
			LOGGER.warn("takeAll predicate yielded nothing", new Throwable());
		}
		return ret;
	}
}
