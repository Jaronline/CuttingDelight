package dev.jaronline.cuttingdelight.common.block.entity;

import dev.jaronline.cuttingdelight.Config;
import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import dev.jaronline.cuttingdelight.common.registry.BlockEntityTypeRegistry;
import dev.jaronline.cuttingdelight.mixin.CuttingBoardBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.advancement.CuttingBoardTrigger;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModAdvancements;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class CustomCuttingBoardBlockEntity extends CuttingBoardBlockEntity {
    public CustomCuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public boolean processStoredStackOrItemUsingTool(CuttingBoardRecipe recipe, ItemStack tool, @Nullable Player player) {
        if (Config.PROCESS_STACK.getAsBoolean()) {
            return processStoredStackUsingTool(recipe, tool, player);
        } else {
            return processStoredItemUsingTool(recipe, tool, player);
        }
    }

    public boolean processStoredItemUsingTool(CuttingBoardRecipe recipe, ItemStack toolStack, @Nullable Player player) {
        if (level == null) return false;
        if (isItemCarvingBoard()) return false;

        for (ItemStack resultStack : (recipe.rollResults(this.level.random, EnchantmentHelper.getTagEnchantmentLevel(this.level.holder(Enchantments.FORTUNE).get(), toolStack)))) {
            Direction direction = this.getBlockState().getValue(CuttingBoardBlock.FACING).getCounterClockWise();
            ItemUtils.spawnItemEntity(this.level, resultStack.copy(), (double) this.worldPosition.getX() + (double) 0.5F + (double) direction.getStepX() * 0.2, (double) this.worldPosition.getY() + 0.2, (double) this.worldPosition.getZ() + (double) 0.5F + (double) direction.getStepZ() * 0.2, (double) ((float) direction.getStepX() * 0.2F), 0.0F, (float) direction.getStepZ() * 0.2F);
        }

        if (!this.level.isClientSide) {
            toolStack.hurtAndBreak(1, (ServerLevel) this.level, player, (item) -> {});
        }

        playProcessingSound(recipe.getSoundEvent().orElse(null), toolStack, getStoredItem());
        removeItem();
        if (player instanceof ServerPlayer) {
            ModAdvancements.USE_CUTTING_BOARD.get().trigger((ServerPlayer) player);
        }

        return true;
    }

    private boolean processStoredStackUsingTool(CuttingBoardRecipe recipe, ItemStack toolStack, @Nullable Player player) {
        if (level == null) return false;
        if (isItemCarvingBoard()) return false;

        int itemCount = getStoredItem().getCount();
        if (toolStack.isDamageableItem() && (player == null || !player.hasInfiniteMaterials())) {
            itemCount = Math.min(itemCount, toolStack.getMaxDamage() - toolStack.getDamageValue());
        }

        if (itemCount < 1) {
            CuttingDelight.LOGGER.warn("Cutting Board at {} tried to process with a broken tool!", worldPosition);
            return false;
        }

        List<ItemStack> results = new ArrayList<>();
        int fortuneLevel = EnchantmentHelper.getTagEnchantmentLevel(level.holder(Enchantments.FORTUNE).get(), toolStack);
        for (int i = 0; i < itemCount; i++) {
            results.addAll(recipe.rollResults(level.random, fortuneLevel));
        }

        for (ItemStack resultStack : results) {
            ItemStack stackToAdd = resultStack.copy();
            if (player == null || !player.addItem(stackToAdd)) {
                Direction direction = getBlockState().getValue(CustomCuttingBoardBlock.FACING).getCounterClockWise();
                ItemUtils.spawnItemEntity(level, stackToAdd,
                        worldPosition.getX() + 0.5 + (direction.getStepX() * 0.2), worldPosition.getY() + 0.2, worldPosition.getZ() + 0.5 + (direction.getStepZ() * 0.2),
                        direction.getStepX() * 0.2F, 0.0F, direction.getStepZ() * 0.2F);
            }
        }
        if (!level.isClientSide) {
            toolStack.hurtAndBreak(itemCount, (ServerLevel) level, player, (item) -> {
            });
        }

        playProcessingSound(recipe.getSoundEvent().orElse(null), toolStack, getStoredItem());
        removeItem(itemCount);
        if (player instanceof ServerPlayer) {
            ModAdvancements.USE_CUTTING_BOARD.get().trigger((ServerPlayer) player);
        }

        return true;
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return BlockEntityTypeRegistry.getInstance().cuttingBoard.get();
    }

    public boolean addStack(ItemStack itemStack) {
        if (this.isEmpty() && !itemStack.isEmpty()) {
            CuttingBoardBlockEntityAccessor accessor = (CuttingBoardBlockEntityAccessor) this;
            accessor.getInventory().setStackInSlot(0, itemStack.split(itemStack.getCount()));
            accessor.setItemCarvingBoard(false);
            this.inventoryChanged();
            return true;
        } else {
            return false;
        }
    }

    public ItemStack removeItem(int count) {
        if (!this.isEmpty()) {
            CuttingBoardBlockEntityAccessor accessor = (CuttingBoardBlockEntityAccessor) this;
            accessor.setItemCarvingBoard(false);
            ItemStack item = this.getStoredItem().split(count);
            this.inventoryChanged();
            return item;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public ItemStack removeStack() {
        return removeItem(this.getStoredItem().getCount());
    }

    public void empty() {
        CuttingBoardBlockEntityAccessor accessor = (CuttingBoardBlockEntityAccessor) this;
        accessor.getInventory().setStackInSlot(0, ItemStack.EMPTY);
    }
}
