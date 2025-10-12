package dev.jaronline.cuttingdelight.common.block.entity;

import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import dev.jaronline.cuttingdelight.common.registry.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModAdvancements;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import java.util.List;

public class CustomCuttingBoardBlockEntity extends CuttingBoardBlockEntity {
    public CustomCuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public boolean processStoredItemUsingTool(CuttingBoardRecipe recipe, ItemStack toolStack, @Nullable Player player) {
        if (level == null) return false;
        if (isItemCarvingBoard()) return false;

        List<ItemStack> results = recipe.rollResults(level.random, EnchantmentHelper.getTagEnchantmentLevel(level.holder(Enchantments.FORTUNE).get(), toolStack));
        for (ItemStack resultStack : results) {
            Direction direction = getBlockState().getValue(CustomCuttingBoardBlock.FACING).getCounterClockWise();
            ItemUtils.spawnItemEntity(level, resultStack.copy(),
                    worldPosition.getX() + 0.5 + (direction.getStepX() * 0.2), worldPosition.getY() + 0.2, worldPosition.getZ() + 0.5 + (direction.getStepZ() * 0.2),
                    direction.getStepX() * 0.2F, 0.0F, direction.getStepZ() * 0.2F);
        }
        if (!level.isClientSide) {
            toolStack.hurtAndBreak(1, (ServerLevel) level, player, (item) -> {});
        }

        playProcessingSound(recipe.getSoundEvent().orElse(null), toolStack, getStoredItem());
        removeItem();
        if (player instanceof ServerPlayer) {
            ModAdvancements.USE_CUTTING_BOARD.get().trigger((ServerPlayer) player);
        }

        return true;
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return BlockEntityTypeRegistry.getInstance().cuttingBoard.get();
    }
}
