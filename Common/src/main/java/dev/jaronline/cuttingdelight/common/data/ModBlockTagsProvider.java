package dev.jaronline.cuttingdelight.common.data;

import dev.jaronline.cuttingdelight.common.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import vectorwing.farmersdelight.common.tag.CompatibilityTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {
	public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		//noinspection deprecation
		super(output, Registries.BLOCK, lookupProvider, (block) -> block.builtInRegistryHolder().key());
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.CUTTING_BOARD);
		tag(CompatibilityTags.CREATE_BRITTLE).add(ModBlocks.CUTTING_BOARD);
	}
}
