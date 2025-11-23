package dev.jaronline.cuttingdelight.server;

import dev.jaronline.cuttingdelight.CuttingDelight;
import dev.jaronline.cuttingdelight.client.gui.menu.CuttingBoardMenu;
import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.common.network.CutPayload;
import dev.jaronline.cuttingdelight.common.network.CuttingBoardEmptiedPayload;
import dev.jaronline.cuttingdelight.common.network.CuttingBoardFilledPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

public class ServerPayloadHandler {
    public static void handleCut(final CutPayload payload, final IPayloadContext context) {
        Player player = context.player();
        BlockEntity blockEntity = player.level().getBlockEntity(payload.blockPos());
        if (!(blockEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardEntity)) {
            throw new IllegalArgumentException("Expected CustomCuttingBoardBlockEntity but found: " + blockEntity.getClass().getSimpleName());
        }
        if (!(payload.recipe() instanceof CuttingBoardRecipe cuttingRecipe)) {
            throw new IllegalArgumentException("Expected CuttingBoardRecipe but found: " + payload.recipe().getClass().getSimpleName());
        }
        cuttingBoardEntity.processStoredStackUsingTool(cuttingRecipe, player.getMainHandItem(), player);
        if (player.containerMenu instanceof CuttingBoardMenu cuttingBoardMenu) {
            cuttingBoardMenu.updateInputItem(cuttingBoardEntity.getStoredItem());
        } else {
            CuttingDelight.LOGGER.warn("Player is not viewing CuttingBoardMenu while cutting on Cutting Board at {}", payload.blockPos());
        }
    }

    public static void handleCuttingBoardEmptied(final CuttingBoardEmptiedPayload payload, final IPayloadContext context) {
        BlockEntity blockEntity = context.player().level().getBlockEntity(payload.blockPos());
        if (!(blockEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardEntity)) {
            throw new IllegalArgumentException("Expected CustomCuttingBoardBlockEntity but found: " + blockEntity.getClass().getSimpleName());
        }
        cuttingBoardEntity.empty();
    }

    public static void handleCuttingBoardFilled(final CuttingBoardFilledPayload payload, final IPayloadContext context) {
        BlockEntity blockEntity = context.player().level().getBlockEntity(payload.blockPos());
        if (!(blockEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardEntity)) {
            throw new IllegalArgumentException("Expected CustomCuttingBoardBlockEntity but found: " + blockEntity.getClass().getSimpleName());
        }
        cuttingBoardEntity.addStack(payload.itemStack());
    }
}
