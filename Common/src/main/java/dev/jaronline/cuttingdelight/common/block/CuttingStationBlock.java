package dev.jaronline.cuttingdelight.common.block;

import dev.jaronline.cuttingdelight.common.ModBlockEntityTypes;
import dev.jaronline.cuttingdelight.common.block.entity.CuttingStationBlockEntity;
import dev.jaronline.cuttingdelight.common.client.gui.menu.CuttingStationMenu;
import dev.jaronline.cuttingdelight.common.event.RightClickBlockEvent;
import dev.jaronline.cuttingdelight.common.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;

public class CuttingStationBlock extends CuttingBoardBlock {
	public CuttingStationBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		CuttingStationBlockEntity blockEntity = (CuttingStationBlockEntity) level.getBlockEntity(pos);
		Container container = Services.PLATFORM.getInventoryHelper().asContainer(blockEntity);
		return new SimpleMenuProvider((containerId, playerInventory, player) ->
				new CuttingStationMenu(containerId, playerInventory, container, ContainerLevelAccess.create(level, pos)),
				getName());
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof CuttingStationBlockEntity cuttingBoard)) {
			return InteractionResult.PASS;
		}

		ItemStack mainHandStack = player.getMainHandItem();

		if (mainHandStack.isEmpty()) {
			if (cuttingBoard.isEmpty() || level.isClientSide) {
				return InteractionResult.CONSUME;
			}

			ItemStack removedStack = cuttingBoard.removeItem();
			if (!player.isCreative()) {
				player.getInventory().add(removedStack);
			}
			Vec3 centerPos = pos.getCenter();
			level.playSound(null, centerPos.x(), centerPos.y(), centerPos.z(),
					Services.PLATFORM.getObjectHelper()
							.getSoundEvent(FarmersDelight.MODID + ":block.cutting_board.remove_item"),
					SoundSource.BLOCKS, 0.25F, 0.5F);
			return InteractionResult.SUCCESS;
		}

		if (cuttingBoard.canAddItem(mainHandStack)) {
			if (level.isClientSide) {
				return InteractionResult.CONSUME;
			}

			ItemStack remainderStack = cuttingBoard.addItem(player.getAbilities().instabuild ? mainHandStack.copy() : mainHandStack);
			if (!player.isCreative()) {
				player.setItemSlot(EquipmentSlot.MAINHAND, remainderStack);
			}
			Vec3 centerPos = pos.getCenter();
			level.playSound(null, centerPos.x(), centerPos.y(), centerPos.z(),
					Services.PLATFORM.getObjectHelper()
							.getSoundEvent(FarmersDelight.MODID + ":block.cutting_board.place_item"),
					SoundSource.BLOCKS, 1.0F, 0.8F);
			return InteractionResult.SUCCESS;
		}

		if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
			serverPlayer.openMenu(state.getMenuProvider(level, pos));
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntityTypes.CUTTING_STATION.create(pos, state);
	}

	public static class ToolCarvingEvent {
		public static void onSneakPlaceTool(RightClickBlockEvent event) {
			Level level = event.getLevel();
			BlockPos pos = event.getPos();

			if (!(level.getBlockEntity(pos) instanceof CuttingStationBlockEntity cuttingBoard)) {
				return;
			}

			Player player = event.getEntity();
			ItemStack heldStack = player.getMainHandItem();

			if (!player.isSecondaryUseActive() || heldStack.isEmpty()) {
				return;
			}

			if (cuttingBoard.carveToolOnBoard(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
				if (!player.isCreative()) {
					player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
				Vec3 centerPos = pos.getCenter();
				level.playSound(null, centerPos.x(), centerPos.y(), centerPos.z(),
						Services.PLATFORM.getObjectHelper()
								.getSoundEvent(FarmersDelight.MODID + ":block.cutting_board.carve_tool"),
						SoundSource.BLOCKS, 1.0F, 0.8F);
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);
				return;
			}

			if (cuttingBoard.processStoredItemUsingTool(heldStack, player)) {
				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);
			}
		}
	}
}
