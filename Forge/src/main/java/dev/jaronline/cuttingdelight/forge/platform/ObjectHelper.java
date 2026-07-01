package dev.jaronline.cuttingdelight.forge.platform;

import com.google.common.reflect.TypeToken;
import dev.jaronline.cuttingdelight.common.platform.PlatformObjectHelper;
import net.minecraft.world.item.crafting.RecipeType;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.item.FuelBlockItem;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.common.registry.ModSounds;

import java.util.Objects;

public final class ObjectHelper extends PlatformObjectHelper {
	@Override
	protected void setObjects() {
		block(CuttingBoardBlock.class).save(() -> (CuttingBoardBlock)ModBlocks.CUTTING_BOARD.get());

		blockItem(FuelBlockItem.class)
				.id(Objects.requireNonNull(ModItems.CUTTING_BOARD.getId()))
				.save(() -> (FuelBlockItem)ModItems.CUTTING_BOARD.get());

		recipeType(new TypeToken<RecipeType<CuttingBoardRecipe>>() {}).save(ModRecipeTypes.CUTTING);

		soundEvent()
				.id(Objects.requireNonNull(ModSounds.BLOCK_CUTTING_BOARD_REMOVE.getId()))
				.save(ModSounds.BLOCK_CUTTING_BOARD_REMOVE);
		soundEvent()
				.id(Objects.requireNonNull(ModSounds.BLOCK_CUTTING_BOARD_PLACE.getId()))
				.save(ModSounds.BLOCK_CUTTING_BOARD_PLACE);
		soundEvent()
				.id(Objects.requireNonNull(ModSounds.BLOCK_CUTTING_BOARD_CARVE.getId()))
				.save(ModSounds.BLOCK_CUTTING_BOARD_CARVE);
	}
}
