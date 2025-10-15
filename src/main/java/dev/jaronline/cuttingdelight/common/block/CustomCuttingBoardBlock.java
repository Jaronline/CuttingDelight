package dev.jaronline.cuttingdelight.common.block;

import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.client.gui.menu.CuttingBoardMenu;
import dev.jaronline.cuttingdelight.common.registry.BlockEntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
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
import vectorwing.farmersdelight.common.tag.ModTags;

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
            ItemStack offhandStack = player.getOffhandItem();

            if (cuttingBoardEntity.isEmpty()) {
                if (!offhandStack.isEmpty()) {
                    if (hand.equals(InteractionHand.MAIN_HAND) && !offhandStack.is(ModTags.OFFHAND_EQUIPMENT) && !(heldStack.getItem() instanceof BlockItem)) {
                        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    }

                    if (hand.equals(InteractionHand.OFF_HAND) && offhandStack.is(ModTags.OFFHAND_EQUIPMENT)) {
                        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    }
                }

                if (heldStack.isEmpty()) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }

                if (cuttingBoardEntity.addStack(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
                    level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
                    return ItemInteractionResult.SUCCESS;
                }
            } else if (!heldStack.isEmpty()) {
                if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(state.getMenuProvider(level, pos), pos);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            } else if (hand.equals(InteractionHand.MAIN_HAND)) {
                if (!player.isCreative()) {
                    if (!player.getInventory().add(player.isCrouching() ? cuttingBoardEntity.removeItem() : cuttingBoardEntity.removeStack())) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), player.isCrouching() ? cuttingBoardEntity.removeItem() : cuttingBoardEntity.removeStack());
                    }
                } else if (player.isCrouching()) {
                    cuttingBoardEntity.removeItem();
                } else {
                    cuttingBoardEntity.removeStack();
                }

                level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.25F, 0.5F);
                return ItemInteractionResult.SUCCESS;
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

            if (!player.isSecondaryUseActive() || heldStack.isEmpty() ||
                    !(tileEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardEntity)) {
                return;
            }

            if (cuttingBoardEntity.isEmpty()) {
                if (cuttingBoardEntity.addItem(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
                    level.playSound((Player) null, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
                    event.setCanceled(true);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                }
                return;
            }

            ItemStack boardStack = cuttingBoardEntity.getStoredItem().copy();
            if (cuttingBoardEntity.processStoredItemUsingTool(heldStack, player)) {
                spawnCuttingParticles(level, pos, boardStack, 5);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }
}

