package dev.jaronline.cuttingdelight.block;

import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.gui.menu.CuttingBoardMenu;
import dev.jaronline.cuttingdelight.registry.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;

public class CustomCuttingBoardBlock extends CuttingBoardBlock {
    private final BlockEntityTypeRegistry blockEntityTypes = BlockEntityTypeRegistry.getInstance();

	public CustomCuttingBoardBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		return new SimpleMenuProvider((containerId, playerInventory, player) ->
				new CuttingBoardMenu(containerId, playerInventory, blockEntity),
				getName());
	}

	@Override
	public @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level,
													@NotNull BlockPos pos, @NotNull Player player,
													@NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
		BlockEntity tileEntity = level.getBlockEntity(pos);

		if (tileEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardEntity) {
			ItemStack heldStack = player.getItemInHand(hand);


			if (!cuttingBoardEntity.isEmpty() && !heldStack.isEmpty()) {
				if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
					serverPlayer.openMenu(state.getMenuProvider(level, pos), pos);
				}
				return ItemInteractionResult.sidedSuccess(level.isClientSide);
			}
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hit);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return blockEntityTypes.cuttingBoard.get().create(pos, state);
	}

	@EventBusSubscriber(modid = CuttingDelight.MOD_ID)
	@SuppressWarnings("unused")
	public static class ToolCarvingEvent {
		@SubscribeEvent
		public static void onSneakPlaceTool(PlayerInteractEvent.RightClickBlock event) {
			Level level = event.getLevel();
			BlockPos pos = event.getPos();
			Player player = event.getEntity();
			ItemStack heldStack = player.getMainHandItem();
			BlockEntity tileEntity = level.getBlockEntity(event.getPos());

			if (player.isSecondaryUseActive() && !heldStack.isEmpty()
					&& tileEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardEntity) {
				if (!cuttingBoardEntity.isEmpty()) {
					ItemStack boardStack = cuttingBoardEntity.getStoredItem().copy();
                    if (cuttingBoardEntity.processStoredItemUsingTool(heldStack, player)) {
                        spawnCuttingParticles(level, pos, boardStack, 5);
						event.setCanceled(true);
						event.setCancellationResult(InteractionResult.SUCCESS);
                    }
                }
			}
		}
	}
}

