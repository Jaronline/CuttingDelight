package dev.jaronline.cuttingdelight.forge.data;

import dev.jaronline.cuttingdelight.common.ModBlocks;
import dev.jaronline.cuttingdelight.common.ModIds;
import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Function;

public class BlockStates extends BlockStateProvider {
	private static final int DEFAULT_ANGLE_OFFSET = 180;

	public BlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, ModIds.CUTTING_DELIGHT_ID, exFileHelper);
	}

	private String blockName(Block block) {
		return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();
	}

	public ResourceLocation resourceBlock(String path) {
		return ModIds.cuttingDelightResource("block/" + path);
	}

	public ModelFile existingModel(Block block) {
		return existingModel(blockName(block));
	}

	public ModelFile existingModel(String path) {
		return new ModelFile.ExistingModelFile(resourceBlock(path), models().existingFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		customHorizontalBlock(ModBlocks.CUTTING_BOARD,
				$ -> existingModel(ModBlocks.CUTTING_BOARD), CustomCuttingBoardBlock.WATERLOGGED);
	}

	public void customHorizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc, Property<?>... ignored) {
		getVariantBuilder(block)
				.forAllStatesExcept(state -> ConfiguredModel.builder()
						.modelFile(modelFunc.apply(state))
						.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + DEFAULT_ANGLE_OFFSET) % 360)
						.build(), ignored);
	}
}
