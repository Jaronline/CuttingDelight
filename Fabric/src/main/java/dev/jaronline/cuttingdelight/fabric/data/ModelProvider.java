package dev.jaronline.cuttingdelight.fabric.data;

import dev.jaronline.cuttingdelight.common.ModBlocks;
import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class ModelProvider extends FabricModelProvider {
	public ModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
		blockStateModelGenerator.blockStateOutput.accept(createCuttingBoardBlockStates());
	}

	private BlockStateGenerator createCuttingBoardBlockStates() {
		return MultiVariantGenerator.multiVariant(ModBlocks.CUTTING_BOARD)
				.with(PropertyDispatch.property(CustomCuttingBoardBlock.FACING)
						.select(Direction.NORTH, modelVariant(ModBlocks.CUTTING_BOARD))
						.select(Direction.EAST, modelVariant(ModBlocks.CUTTING_BOARD).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
						.select(Direction.SOUTH, modelVariant(ModBlocks.CUTTING_BOARD).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
						.select(Direction.WEST, modelVariant(ModBlocks.CUTTING_BOARD).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
				);
	}

	private Variant modelVariant(Block block) {
		return Variant.variant().with(VariantProperties.MODEL, resourceBlock(blockName(block)));
	}

	private String blockName(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block).getPath();
	}

	private ResourceLocation resourceBlock(String path) {
		return ModIds.cuttingDelightResource("block/" + path);
	}

	@Override
	public void generateItemModels(ItemModelGenerators itemModelGenerator) {

	}

	@Override
	public String getName() {
		return "MiniMashModelProvider";
	}
}
