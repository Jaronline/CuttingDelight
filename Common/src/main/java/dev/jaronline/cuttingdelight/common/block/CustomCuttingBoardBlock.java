package dev.jaronline.cuttingdelight.common.block;

import dev.jaronline.cuttingdelight.common.ModBlockEntityTypes;
import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.common.client.gui.menu.CuttingBoardMenu;
import dev.jaronline.cuttingdelight.common.provider.ProviderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.tag.ModTags;

public class CustomCuttingBoardBlock extends CuttingBoardBlock {
	public CustomCuttingBoardBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		CustomCuttingBoardBlockEntity blockEntity = (CustomCuttingBoardBlockEntity) level.getBlockEntity(pos);
		Container container = ProviderManager.getInventoryProvider().asContainer(blockEntity);
		return new SimpleMenuProvider((containerId, playerInventory, player) ->
				new CuttingBoardMenu(containerId, playerInventory, container, ContainerLevelAccess.create(level, pos)),
				getName());
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity tileEntity = level.getBlockEntity(pos);

		if (tileEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardBlockEntity) {
			ItemStack heldStack = player.getItemInHand(hand);
			ItemStack offhandStack = player.getOffhandItem();

			if (cuttingBoardBlockEntity.isEmpty()) {
				if (!offhandStack.isEmpty()) {
					if (hand.equals(InteractionHand.MAIN_HAND) && !offhandStack.is(ModTags.OFFHAND_EQUIPMENT) && !(heldStack.getItem() instanceof BlockItem)) {
						return InteractionResult.PASS;
					}

					if (hand.equals(InteractionHand.OFF_HAND) && offhandStack.is(ModTags.OFFHAND_EQUIPMENT)) {
						return InteractionResult.PASS;
					}
				}

				if (heldStack.isEmpty()) {
					return InteractionResult.PASS;
				}

				if (cuttingBoardBlockEntity.addStack(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
					level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
					return InteractionResult.SUCCESS;
				}
			} else if (!heldStack.isEmpty()) {
				if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
					serverPlayer.openMenu(state.getMenuProvider(level, pos));
				}
				return InteractionResult.sidedSuccess(level.isClientSide);
			} else if (hand.equals(InteractionHand.MAIN_HAND)) {
				if (!player.isCreative()) {
					ItemStack removed = player.isCrouching() ? cuttingBoardBlockEntity.removeItem() : cuttingBoardBlockEntity.removeStack();
					if (!player.getInventory().add(removed)) {
						Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), removed);
					}
				} else if (player.isCrouching()) {
					cuttingBoardBlockEntity.removeItem();
				} else {
					cuttingBoardBlockEntity.removeStack();
				}

				level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.25F, 0.5F);
				return InteractionResult.SUCCESS;
			}
		}

		return super.use(state, level, pos, player, hand, hit);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntityTypes.CUTTING_BOARD.create(pos, state);
	}

	public static class ToolCarvingEvent {
		public static InteractionResult onSneakPlaceTool(Level level, BlockPos pos, Player player) {
			ItemStack heldStack = player.getMainHandItem();
			BlockEntity tileEntity = level.getBlockEntity(pos);

			if (!player.isSecondaryUseActive() || heldStack.isEmpty() ||
					!(tileEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardBlockEntity)) {
				return InteractionResult.PASS;
			}

			if (cuttingBoardBlockEntity.isEmpty()) {
				ItemStack stackToAdd = player.getAbilities().instabuild ? heldStack.copy() : heldStack;
				boolean canCarveTool = heldStack.getItem() instanceof TieredItem || heldStack.getItem() instanceof TridentItem
						|| heldStack.getItem() instanceof ShearsItem;
				if ((canCarveTool && cuttingBoardBlockEntity.carveToolOnBoard(stackToAdd)) || cuttingBoardBlockEntity.addItem(stackToAdd)) {
					level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
				}
				return InteractionResult.SUCCESS;
			}

			ItemStack boardStack = cuttingBoardBlockEntity.getStoredItem().copy();
			if (cuttingBoardBlockEntity.processStoredItemUsingTool(heldStack, player)) {
				spawnCuttingParticles(level, pos, boardStack, 5);;
				return InteractionResult.SUCCESS;
			}

			return InteractionResult.PASS;
		}
	}
}
