package dev.jaronline.cuttingdelight.fabric.data;

import dev.jaronline.cuttingdelight.common.data.ModBlockTagsProvider;
import dev.jaronline.cuttingdelight.common.data.ModRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModBlockTagsProvider::new);
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(BlockLootTableProvider::new);
		pack.addProvider(ModelProvider::new);
	}
}
