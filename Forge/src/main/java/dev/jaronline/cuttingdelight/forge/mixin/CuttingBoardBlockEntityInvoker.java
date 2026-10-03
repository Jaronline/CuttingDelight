package dev.jaronline.cuttingdelight.forge.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import java.util.Optional;

@Mixin(CuttingBoardBlockEntity.class)
public interface CuttingBoardBlockEntityInvoker {
	@Invoker("getMatchingRecipe")
	Optional<CuttingBoardRecipe> cuttingdelight$getMatchingRecipe(RecipeWrapper recipeWrapper, ItemStack toolStack, @Nullable Player player);
}
