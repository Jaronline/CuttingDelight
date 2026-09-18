package dev.jaronline.cuttingdelight.common.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

import javax.annotation.Nullable;
import java.util.Optional;

@Mixin(CuttingBoardBlockEntity.class)
public interface CuttingBoardBlockEntityAccessor {
	@Accessor("isItemCarvingBoard")
	void setItemCarvingBoard(boolean value);
	@Invoker("getMatchingRecipe")
	Optional<RecipeHolder<CuttingBoardRecipe>> cuttingdelight$getMatchingRecipe(ItemStack toolStack, @Nullable Player player);
}
