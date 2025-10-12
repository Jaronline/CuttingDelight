package dev.jaronline.cuttingdelight.server;

import dev.jaronline.cuttingdelight.common.block.entity.CustomCuttingBoardBlockEntity;
import dev.jaronline.cuttingdelight.network.CutPayload;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;

public class ServerPayloadHandler {
    public static void handleCut(final CutPayload payload, final IPayloadContext context) {
        BlockEntity blockEntity = context.player().level().getBlockEntity(payload.blockPos());
        if (!(blockEntity instanceof CustomCuttingBoardBlockEntity cuttingBoardEntity)) {
            throw new IllegalArgumentException("BlockEntity must be instance of CustomCuttingBoardBlockEntity");
        }
        if (!(payload.recipe() instanceof CuttingBoardRecipe cuttingRecipe)) {
            throw new IllegalArgumentException("Recipe must be instance of CuttingBoardRecipe");
        }
        cuttingBoardEntity.processStoredItemUsingTool(cuttingRecipe, context.player().getMainHandItem(), context.player());
    }
}
