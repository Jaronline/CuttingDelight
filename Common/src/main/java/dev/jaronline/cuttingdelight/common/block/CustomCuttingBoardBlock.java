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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
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
    protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((containerId, playerInventory, player) ->
                new CuttingBoardMenu(containerId, playerInventory, ContainerLevelAccess.create(level, pos)),
                getName());
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        
        if (tileEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardBlockEntity) {
            ItemStack heldStack = player.getItemInHand(hand);
            ItemStack offhandStack = player.getOffhandItem();
            
            if (cuttingBoardBlockEntity.isEmpty()) {
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
                
                if (cuttingBoardBlockEntity.addStack(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
                    level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
                    return ItemInteractionResult.SUCCESS;
                }
            } else if (!heldStack.isEmpty()) {
                if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                    ProviderManager.getPlayerProvider().openMenu(serverPlayer, state.getMenuProvider(level, pos), pos);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
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
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hit);
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
                if (cuttingBoardBlockEntity.addItem(player.getAbilities().instabuild ? heldStack.copy() : heldStack)) {
                    level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 0.8F);
                }
                return InteractionResult.SUCCESS;
            }

            ItemStack boardStack = cuttingBoardBlockEntity.getStoredItem().copy();
            if (cuttingBoardBlockEntity.processStoredItemUsingTool(heldStack, player)) {
                spawnCuttingParticles(level, pos, boardStack, 5);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }
    }
}
