package dev.jaronline.cuttingdelight.common.block;

import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.common.registry.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
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
	public @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, Level level,
													@NotNull BlockPos pos, @NotNull Player player,
													@NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
		BlockEntity tileEntity = level.getBlockEntity(pos);

		if (tileEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardEntity) {
			ItemStack heldStack = player.getItemInHand(hand);

			if (!cuttingBoardEntity.isEmpty() && !heldStack.isEmpty()) {
				return ItemInteractionResult.CONSUME;
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

