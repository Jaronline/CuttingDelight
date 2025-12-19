package dev.jaronline.cuttingdelight.common.data;

import dev.jaronline.cuttingdelight.common.ModBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseBlockLoot extends BlockLootSubProvider {
	protected final Set<Block> generatedLootTables = new HashSet<>();

	protected BaseBlockLoot() {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags());
	}

	@Override
	protected void generate() {
		dropSelf(ModBlocks.CUTTING_STATION);
	}

	@Override
	protected void add(Block block, LootTable.Builder builder) {
		this.generatedLootTables.add(block);
		this.map.put(block.getLootTable(), builder);
	}
}
