package dev.jaronline.cuttingdelight.neoforge.data;

import dev.jaronline.cuttingdelight.common.ModBlocks;
import dev.jaronline.cuttingdelight.common.ModIds;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.tag.CompatibilityTags;

import java.util.concurrent.CompletableFuture;

public class BlockTags extends BlockTagsProvider {
	public BlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, ModIds.CUTTING_DELIGHT_ID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.@NotNull Provider provider) {
		tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(ModBlocks.CUTTING_BOARD);
		tag(CompatibilityTags.CREATE_BRITTLE).add(ModBlocks.CUTTING_BOARD);
	}
}
