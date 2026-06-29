package dev.jaronline.cuttingdelight.common.block;

import dev.jaronline.cuttingdelight.common.ModBlockEntityTypes;
import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.common.client.gui.menu.CuttingBoardMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.registry.ModSounds;

public class CustomCuttingBoardBlock extends CuttingBoardBlock {
	public CustomCuttingBoardBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider((containerId, playerInventory, player) ->
				new CuttingBoardMenu(containerId, playerInventory, ContainerLevelAccess.create(level, pos)),
				getName());
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity tileEntity = level.getBlockEntity(pos);

        if (!(tileEntity instanceof CustomCuttingBoardBlockEntity cuttingBoard))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        ItemStack mainHandStack = player.getMainHandItem();

        if (mainHandStack.isEmpty()) {
            if (cuttingBoard.isEmpty() || level.isClientSide)
                return ItemInteractionResult.CONSUME;

            ItemStack removedStack = cuttingBoard.removeItem();
            if (!player.isCreative()) {
                player.getInventory().add(removedStack);
            }

            Vec3 centerPos = pos.getCenter();
            level.playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), ModSounds.BLOCK_CUTTING_BOARD_REMOVE.get(), SoundSource.BLOCKS, 0.25F, 0.5F);
            return ItemInteractionResult.SUCCESS;
        }

        if (cuttingBoard.canAddItem(mainHandStack)) {
            if (level.isClientSide)
                return ItemInteractionResult.CONSUME;

            ItemStack remainderStack = cuttingBoard.addItem(player.getAbilities().instabuild ? mainHandStack.copy() : mainHandStack);
            if (!player.isCreative()) {
                player.setItemSlot(EquipmentSlot.MAINHAND, remainderStack);
            }

            Vec3 centerPos = pos.getCenter();
            level.playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), ModSounds.BLOCK_CUTTING_BOARD_PLACE.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
            return ItemInteractionResult.SUCCESS;
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(state.getMenuProvider(level, pos));
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return ModBlockEntityTypes.CUTTING_BOARD.create(pos, state);
	}

    public static class ToolCarvingEvent {
        // FIXME: The parameters should be changed in a future version after 1.1.0 to use something more event-like
        public static InteractionResult onSneakPlaceTool(Level level, BlockPos pos, Player player) {
            ItemStack heldStack = player.getMainHandItem();
            BlockEntity tileEntity = level.getBlockEntity(pos);

			if (!player.isSecondaryUseActive() || heldStack.isEmpty() ||
					!(tileEntity instanceof CustomCuttingBoardBlockEntity cuttingBoard)) {
				return InteractionResult.PASS;
			}

            if (cuttingBoard.carveToolOnBoard(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
                if (!player.isCreative()) {
                    player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                }

                Vec3 centerPos = pos.getCenter();
                level.playSound(null, centerPos.x(), centerPos.y(), centerPos.z(), ModSounds.BLOCK_CUTTING_BOARD_CARVE.get(), SoundSource.BLOCKS, 1.0F, 0.8F);
                return InteractionResult.SUCCESS;
            }

            return cuttingBoard.processStoredItemUsingTool(heldStack, player) ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
    }
}
