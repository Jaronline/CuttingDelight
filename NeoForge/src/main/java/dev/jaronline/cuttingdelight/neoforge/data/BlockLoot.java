package dev.jaronline.cuttingdelight.neoforge.data;

import dev.jaronline.cuttingdelight.common.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public class BlockLoot extends BlockLootSubProvider {
	protected final Set<Block> generatedLootTables = new HashSet<>();

	protected BlockLoot(HolderLookup.Provider registries) {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
	}

	@Override
	public void generate() {
		dropSelf(ModBlocks.CUTTING_BOARD);
	}

	@Override
	public void add(Block block, LootTable.Builder builder) {
		this.generatedLootTables.add(block);
		this.map.put(block.getLootTable(), builder);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return generatedLootTables;
	}
}
