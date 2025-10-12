package dev.jaronline.cuttingdelight.data;

import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.common.registry.BlockRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;

import java.util.function.Function;

public class BlockStates extends BlockStateProvider {
	private static final int DEFAULT_ANGLE_OFFSET = 180;

    private final BlockRegistry blocks = BlockRegistry.getInstance();

    public BlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CuttingDelight.MOD_ID, exFileHelper);
    }

	private String blockName(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block).getPath();
	}

	public ResourceLocation resourceBlock(String path) {
		return ResourceLocation.fromNamespaceAndPath(CuttingDelight.MOD_ID, "block/" + path);
	}

	public ModelFile existingModel(Block block) {
		return new ModelFile.ExistingModelFile(resourceBlock(blockName(block)), models().existingFileHelper);
	}

	public ModelFile existingModel(String path) {
		return new ModelFile.ExistingModelFile(resourceBlock(path), models().existingFileHelper);
	}

    @Override
    protected void registerStatesAndModels() {
		customHorizontalBlock(blocks.cuttingBoard.get(),
				$ -> existingModel(blocks.cuttingBoard.get()), CuttingBoardBlock.WATERLOGGED);
    }

	public void customHorizontalBlock(Block block, Function<BlockState, ModelFile> modelFunc, Property<?>... ignored) {
		getVariantBuilder(block)
				.forAllStatesExcept(state -> ConfiguredModel.builder()
						.modelFile(modelFunc.apply(state))
						.rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + DEFAULT_ANGLE_OFFSET) % 360)
						.build(), ignored);
	}
}
