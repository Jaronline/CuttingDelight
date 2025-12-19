package dev.jaronline.cuttingdelight.common.block.entity;

import com.mojang.logging.LogUtils;
import dev.jaronline.cuttingdelight.common.ModBlockEntityTypes;
import dev.jaronline.cuttingdelight.common.block.CustomCuttingBoardBlock;
import dev.jaronline.cuttingdelight.common.config.ConfigManager;
import dev.jaronline.cuttingdelight.common.mixin.CuttingBoardBlockEntityAccessor;
import dev.jaronline.cuttingdelight.common.provider.ProviderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModAdvancements;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CustomCuttingBoardBlockEntity extends CuttingBoardBlockEntity {
	private static final Logger LOGGER = LogUtils.getLogger();

	public CustomCuttingBoardBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public BlockEntityType<?> getType() {
		return ModBlockEntityTypes.CUTTING_BOARD;
	}

	public boolean processStoredStackOrItemUsingTool(CuttingBoardRecipe recipe, ItemStack tool, @Nullable Player player) {
		if (ConfigManager.getConfig().shouldProcessStack()) {
			return processStoredStackUsingTool(recipe, tool, player);
		}
		return processStoredItemUsingTool(recipe, tool, player);
	}

	private boolean processStoredItemUsingTool(CuttingBoardRecipe recipe, ItemStack toolStack, @Nullable Player player) {
		if (level == null) return false;
		if (isItemCarvingBoard()) return false;

		List<ItemStack> results = recipe.rollResults(level.random, getFortuneLevel(toolStack));
		processToolResults(results, recipe.getSoundEventID(), toolStack, player);

		return true;
	}

	private boolean processStoredStackUsingTool(CuttingBoardRecipe recipe, ItemStack toolStack, @Nullable Player player) {
		if (level == null) return false;
		if (isItemCarvingBoard()) return false;

		int itemCount = getStoredItem().getCount();
		if (toolStack.isDamageableItem() && (player == null || !player.getAbilities().instabuild)) {
			itemCount = Math.min(itemCount, toolStack.getMaxDamage() - toolStack.getDamageValue());
		}

		if (itemCount < 1) {
			LOGGER.warn("Cutting Board at {} tried to process with a broken tool!", worldPosition);
			return false;
		}

		List<ItemStack> results = new ArrayList<>();
		int fortuneLevel = getFortuneLevel(toolStack);
		for (int i = 0; i < itemCount; i++) {
			results.addAll(recipe.rollResults(level.random, fortuneLevel));
		}

		processToolResults(results, itemCount, recipe.getSoundEventID(), toolStack, player);
		return true;
	}

	private void processToolResults(List<ItemStack> results, String soundEventID, ItemStack toolStack, @Nullable Player player) {
		processToolResults(results, 1, soundEventID, toolStack, player);
	}

	private void processToolResults(List<ItemStack> results, int itemCount, String soundEventID, ItemStack toolStack, @Nullable Player player) {
		assert level != null;
		for (ItemStack resultStack : results) {
			ItemStack stackToAdd = resultStack.copy();
			if (player == null || !player.addItem(stackToAdd)) {
				Direction direction = getBlockState().getValue(CustomCuttingBoardBlock.FACING).getCounterClockWise();
				ItemUtils.spawnItemEntity(level, stackToAdd,
						worldPosition.getX()  + 0.5 + (direction.getStepX() * 0.2), worldPosition.getY() + 0.2, worldPosition.getZ() + 0.5 + (direction.getStepZ() * 0.2),
						direction.getStepX() * 0.2F, 0.0F, direction.getStepZ() * 0.2F);
			}
		}
		if (!level.isClientSide) {
			toolStack.hurtAndBreak(itemCount, (ServerPlayer) player, (item) -> {});
		}

		playProcessingSound(soundEventID, toolStack, getStoredItem());
		removeItem(itemCount);
		if (player instanceof ServerPlayer) {
			ModAdvancements.CUTTING_BOARD.trigger((ServerPlayer) player);
		}
	}

	private int getFortuneLevel(ItemStack toolStack) {
		if (toolStack.isEmpty()) {
			return 0;
		}

		ResourceLocation resourceLocation = EnchantmentHelper.getEnchantmentId(Enchantments.BLOCK_FORTUNE);
		ListTag listTag = toolStack.getEnchantmentTags();

		for (int i = 0; i < listTag.size(); i++) {
			CompoundTag compoundTag = listTag.getCompound(i);
			ResourceLocation otherResourceLocation = EnchantmentHelper.getEnchantmentId(compoundTag);
			if (otherResourceLocation != null && otherResourceLocation.equals(resourceLocation)) {
				return EnchantmentHelper.getEnchantmentLevel(compoundTag);
			}
		}

		return 0;
	}

	public boolean addStack(ItemStack itemStack) {
		if (!this.isEmpty() || itemStack.isEmpty()) {
			return false;
		}
		ProviderManager.getInventoryProvider().setStackInSlot(this, 0, itemStack.split(itemStack.getCount()));
		CuttingBoardBlockEntityAccessor accessor = (CuttingBoardBlockEntityAccessor) this;
		accessor.setItemCarvingBoard(false);
		this.inventoryChanged();
		return true;
	}

	public ItemStack removeItem(int count) {
		if (count == 1) return removeItem();
		if (this.isEmpty() || count < 1) return ItemStack.EMPTY;

		CuttingBoardBlockEntityAccessor accessor = (CuttingBoardBlockEntityAccessor) this;
		accessor.setItemCarvingBoard(false);
		ItemStack item = this.getStoredItem().split(count);
		this.inventoryChanged();
		return item;
	}

	public ItemStack removeStack() {
		return removeItem(this.getStoredItem().getCount());
	}

	public void empty() {
		ProviderManager.getInventoryProvider().setStackInSlot(this, 0, ItemStack.EMPTY);
	}
}
